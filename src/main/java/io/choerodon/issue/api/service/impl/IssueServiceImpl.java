package io.choerodon.issue.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.IssueDTO;
import io.choerodon.issue.api.dto.IssueFieldValueDTO;
import io.choerodon.issue.api.dto.SearchDTO;
import io.choerodon.issue.api.service.IssueFieldValueService;
import io.choerodon.issue.api.service.IssueService;
import io.choerodon.issue.api.service.StateService;
import io.choerodon.issue.api.service.UserService;
import io.choerodon.issue.domain.Issue;
import io.choerodon.issue.domain.StateMachineSchemeConfig;
import io.choerodon.issue.infra.enums.PageSchemeLineType;
import io.choerodon.issue.infra.enums.SchemeApplyType;
import io.choerodon.issue.infra.enums.SchemeType;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.mapper.*;
import io.choerodon.issue.infra.utils.ProjectUtil;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Arrays;
import java.util.List;

/**
 * @author shinan.chen
 * @Date 2018/9/3
 */
@Component
@RefreshScope
public class IssueServiceImpl extends BaseServiceImpl<Issue> implements IssueService {
    @Value("${spring.application.name:default}")
    private String serverCode;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private IssueFieldValueService issueFieldValueService;
    @Autowired
    private ProjectInfoMapper projectInfoMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;
    @Autowired
    private IssueTypeMapper issueTypeMapper;
    @Autowired
    private IssueTypeSchemeConfigMapper issueTypeSchemeConfigMapper;
    @Autowired
    private ProjectConfigMapper projectConfigMapper;
    @Autowired
    private StateMachineSchemeConfigMapper stateMachineSchemeConfigMapper;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private StateService stateService;
    @Autowired
    private ProjectUtil projectUtil;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public IssueDTO queryById(Long projectId, Long issueId) {
        Issue issue = issueMapper.queryById(projectId, issueId);
        if (issue == null) {
            return null;
        }
        IssueDTO issueDTO = modelMapper.map(issue, IssueDTO.class);
        //加载出问题的字段和值
        List<IssueFieldValueDTO> fieldValues = issueFieldValueService.queryByIssueTypeIdAndPageType(projectId, issue.getIssueTypeId(), PageSchemeLineType.EDIT.value());
        issueFieldValueService.supplyFieldValue(issueId, fieldValues);
        issueDTO.setFieldValues(fieldValues);
        //加载用户信息
        userService.handleUserInfo(Arrays.asList(issueDTO));
        //补全状态信息
        stateService.handleState(projectUtil.getOrganizationId(projectId), Arrays.asList(issueDTO));

        return issueDTO;
    }

    @Override
    public IssueDTO create(Long projectId, IssueDTO issueDTO) {
        //手动开启事物并提交，在Action中，修改状态或者处理一些后置处理时，需要数据库已有的issue数据，做业务操作
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
        TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态
        issueDTO.setProjectId(projectId);
        Issue issue = modelMapper.map(issueDTO, Issue.class);
        try {
            //生成编号
            Integer issueCode = projectInfoMapper.queryByProjectId(projectId).getIssueMaxNum().intValue() + 1;
            issue.setCode(issueCode.toString());
            if (issueMapper.insert(issue) != 1) {
                throw new CommonException("error.issue.create");
            }
            //更新项目最大编号
            projectInfoMapper.updateIssueMaxNum(projectId);
            //创建该问题对应的字段值
            issueFieldValueService.createFieldValues(projectId, issue.getId(), issueDTO.getIssueTypeId(), issueDTO.getFieldValues());
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            e.printStackTrace();
        }
        return queryById(projectId, issue.getId());
    }

    @Override
    public IssueDTO updateIssue(IssueDTO issueDTO, List<String> updateFieldList) {
        Issue issue = modelMapper.map(issueDTO, Issue.class);
        int isUpdate = updateOptional(issue, updateFieldList.toArray(new String[updateFieldList.size()]));

        if (isUpdate != 1) {
            throw new CommonException("error.issue.update");
        }
        return queryById(issueDTO.getProjectId(), issue.getId());
    }

    @Override
    public Page<IssueDTO> pageQuery(Long projectId, PageRequest pageRequest, SearchDTO searchDTO) {
        //处理自定义字段
        issueFieldValueService.handleFieldValueSearch(projectId, searchDTO);
        //处理用户搜索
        userService.handleUserSearch(projectId, searchDTO, "reporterName", "reporterIds");
        userService.handleUserSearch(projectId, searchDTO, "handlerName", "handlerIds");

        Page<Issue> pages = PageHelper.doPageAndSort(pageRequest,
                () -> issueMapper.queryIssuesByArgs(projectId, searchDTO.getSearchArgs(), searchDTO.getSearchArgsIds(), searchDTO.getParam(), searchDTO.getParamIds()));

        Page<IssueDTO> pagesDTO = new Page<>();
        List<IssueDTO> issueDTOS = modelMapper.map(pages.getContent(), new TypeToken<List<IssueDTO>>() {
        }.getType());
        //补全用户信息
        userService.handleUserInfo(issueDTOS);
        //补全状态信息
        stateService.handleState(projectUtil.getOrganizationId(projectId), issueDTOS);

        pagesDTO.setNumber(pages.getNumber());
        pagesDTO.setNumberOfElements(pages.getNumberOfElements());
        pagesDTO.setSize(pages.getSize());
        pagesDTO.setTotalElements(pages.getTotalElements());
        pagesDTO.setTotalPages(pages.getTotalPages());
        pagesDTO.setContent(issueDTOS);
        return pagesDTO;
    }

    @Override
    public Long getStateMachineId(Long projectId, Long issueId) {
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, SchemeApplyType.CLOOPM).getSchemeId();
        if (stateMachineSchemeId == null) {
            throw new CommonException("error.stateMachineSchemeId.null");
        }
        Issue issue = issueMapper.selectByPrimaryKey(issueId);
        if (issue == null) {
            throw new CommonException("error.issue.notFound");
        }
        Long issueTypeId = issue.getIssueTypeId();
        StateMachineSchemeConfig stateMachineSchemeConfig = new StateMachineSchemeConfig();
        stateMachineSchemeConfig.setIssueTypeId(issueTypeId);
        stateMachineSchemeConfig.setSchemeId(stateMachineSchemeId);
        List<StateMachineSchemeConfig> stateMachineSchemeConfigs = stateMachineSchemeConfigMapper.select(stateMachineSchemeConfig);
        if (stateMachineSchemeConfigs == null || stateMachineSchemeConfigs.size() != 1) {
            throw new CommonException("error.stateMachineSchemeConfig.foundError");
        }
        return stateMachineSchemeConfigs.get(0).getStateMachineId();
    }
}
