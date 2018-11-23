package io.choerodon.issue.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.dto.IssueTypeSchemeDTO;
import io.choerodon.issue.api.service.IssueTypeSchemeService;
import io.choerodon.issue.api.service.IssueTypeService;
import io.choerodon.issue.api.service.PriorityService;
import io.choerodon.issue.api.service.ProjectConfigService;
import io.choerodon.issue.domain.IssueType;
import io.choerodon.issue.domain.IssueTypeScheme;
import io.choerodon.issue.domain.IssueTypeSchemeConfig;
import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.issue.infra.enums.InitIssueType;
import io.choerodon.issue.infra.enums.SchemeApplyType;
import io.choerodon.issue.infra.enums.SchemeType;
import io.choerodon.issue.infra.mapper.IssueTypeMapper;
import io.choerodon.issue.infra.mapper.IssueTypeSchemeConfigMapper;
import io.choerodon.issue.infra.mapper.IssueTypeSchemeMapper;
import io.choerodon.issue.infra.mapper.ProjectConfigMapper;
import io.choerodon.issue.infra.utils.EnumUtil;
import io.choerodon.issue.infra.utils.ListChangeUtil;
import io.choerodon.issue.infra.utils.ProjectUtil;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @Date 2018/8/10
 */
@Component
@RefreshScope
public class IssueTypeSchemeServiceImpl extends BaseServiceImpl<IssueTypeScheme> implements IssueTypeSchemeService {

    private IssueTypeSchemeMapper issueTypeSchemeMapper;

    private IssueTypeSchemeConfigMapper issueTypeSchemeConfigMapper;

    private IssueTypeMapper issueTypeMapper;

    @Autowired
    private ProjectConfigMapper projectConfigMapper;
    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private IssueTypeService issueTypeService;
    @Autowired
    private PriorityService priorityService;

    private final ModelMapper modelMapper = new ModelMapper();

    public IssueTypeSchemeServiceImpl(IssueTypeSchemeMapper issueTypeSchemeMapper,
                                      IssueTypeSchemeConfigMapper issueTypeSchemeConfigMapper,
                                      IssueTypeMapper issueTypeMapper) {
        this.issueTypeSchemeMapper = issueTypeSchemeMapper;
        this.issueTypeSchemeConfigMapper = issueTypeSchemeConfigMapper;
        this.issueTypeMapper = issueTypeMapper;
    }

    @Override
    public IssueTypeSchemeDTO queryById(Long organizationId, Long issueTypeSchemeId) {
        IssueTypeScheme issueTypeScheme = issueTypeSchemeMapper.selectByPrimaryKey(issueTypeSchemeId);
        if (issueTypeScheme != null) {
            IssueTypeSchemeDTO issueTypeSchemeDTO = modelMapper.map(issueTypeScheme, IssueTypeSchemeDTO.class);
            //根据方案配置表获取 问题类型
            List<IssueType> issueTypes = issueTypeMapper.queryBySchemeId(organizationId, issueTypeSchemeId);
            issueTypeSchemeDTO.setIssueTypes(modelMapper.map(issueTypes, new TypeToken<List<IssueTypeDTO>>() {
            }.getType()));
            return issueTypeSchemeDTO;
        }
        return null;
    }

    @Override
    @Transactional
    public IssueTypeSchemeDTO create(Long organizationId, IssueTypeSchemeDTO issueTypeSchemeDTO) {
        //创建的均为通用的
        issueTypeSchemeDTO.setApplyType(SchemeApplyType.COMMON);

        if (!checkName(organizationId, issueTypeSchemeDTO.getName(), null)) {
            throw new CommonException("error.issueTypeScheme.name.exist");
        }

        issueTypeSchemeDTO.setOrganizationId(organizationId);
        IssueTypeScheme issueTypeScheme = modelMapper.map(issueTypeSchemeDTO, IssueTypeScheme.class);
        if (issueTypeSchemeMapper.insert(issueTypeScheme) != 1) {
            throw new CommonException("error.issueType.create");
        }
        //创建方案配置
        createConfig(organizationId, issueTypeScheme.getId(), issueTypeSchemeDTO.getIssueTypes());

        return queryById(organizationId, issueTypeScheme.getId());
    }

    @Override
    public IssueTypeSchemeDTO update(Long organizationId, IssueTypeSchemeDTO issueTypeSchemeDTO) {
        issueTypeSchemeDTO.setApplyType(issueTypeSchemeMapper.selectByPrimaryKey(issueTypeSchemeDTO.getId()).getApplyType());
        if (issueTypeSchemeDTO.getName() != null && !checkName(organizationId, issueTypeSchemeDTO.getName(), issueTypeSchemeDTO.getId())) {
            throw new CommonException("error.issueTypeScheme.name.exist");
        }

        IssueTypeScheme issueTypeScheme = modelMapper.map(issueTypeSchemeDTO, IssueTypeScheme.class);
        int isUpdate = issueTypeSchemeMapper.updateByPrimaryKeySelective(issueTypeScheme);
        if (isUpdate != 1) {
            throw new CommonException("error.issueTypeScheme.update");
        }
        //更新方案配置,等待校验[toDo]
        List<IssueType> newIssueTypes = modelMapper.map(issueTypeSchemeDTO.getIssueTypes(), new TypeToken<List<IssueType>>() {
        }.getType());
        List<IssueType> oldIssueTypes = issueTypeMapper.queryBySchemeId(issueTypeSchemeDTO.getOrganizationId(), issueTypeSchemeDTO.getId());

        BiPredicate<IssueType, IssueType> myEquals = (IssueType x1, IssueType x2) -> {
            if (x1.getId() != null && !x1.getId().equals(x2.getId())) {
                return false;
            }
            return true;
        };
        List<IssueType> add = ListChangeUtil.getAddList(newIssueTypes, oldIssueTypes, myEquals);
        List<IssueType> reduce = ListChangeUtil.getReduceList(newIssueTypes, oldIssueTypes, myEquals);

        issueTypeSchemeConfigMapper.deleteBySchemeId(organizationId, issueTypeSchemeDTO.getId());
        createConfig(organizationId, issueTypeScheme.getId(), issueTypeSchemeDTO.getIssueTypes());

        return queryById(organizationId, issueTypeScheme.getId());
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long issueTypeSchemeId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        IssueTypeScheme issueTypeScheme = issueTypeSchemeMapper.selectByPrimaryKey(issueTypeSchemeId);
        if (issueTypeScheme == null) {
            throw new CommonException("error.issueTypeScheme.notFound");
        }
        if (!issueTypeScheme.getOrganizationId().equals(organizationId)) {
            throw new CommonException("error.issueTypeScheme.illegal");
        }
        //判断要删除的issueTypeScheme是否有使用中的项目【toDo】


        return result;
    }

    @Override
    public Boolean delete(Long organizationId, Long issueTypeSchemeId) {
        Map<String, Object> result = checkDelete(organizationId, issueTypeSchemeId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = issueTypeSchemeMapper.deleteByPrimaryKey(issueTypeSchemeId);
            if (isDelete != 1) {
                throw new CommonException("error.issueType.delete");
            }
            issueTypeSchemeConfigMapper.deleteBySchemeId(organizationId, issueTypeSchemeId);
            //关联删除一些东西【toDo】
        } else {
            return false;
        }
        return true;
    }

    @Override
    public Page<IssueTypeSchemeDTO> pageQuery(PageRequest pageRequest, IssueTypeSchemeDTO issueTypeSchemeDTO, String param) {
        IssueTypeScheme issueTypeScheme = modelMapper.map(issueTypeSchemeDTO, IssueTypeScheme.class);

        Page<IssueTypeScheme> pages = PageHelper.doPageAndSort(pageRequest,
                () -> issueTypeSchemeMapper.fulltextSearch(issueTypeScheme, param));
        List<IssueTypeScheme> content = pages.getContent();
        List<IssueTypeSchemeDTO> contentDTO = modelMapper.map(content, new TypeToken<List<IssueTypeSchemeDTO>>() {
        }.getType());
        for (IssueTypeSchemeDTO scheme : contentDTO) {
            //根据方案配置表获取 问题类型
            List<IssueType> issueTypes = issueTypeMapper.queryBySchemeId(scheme.getOrganizationId(), scheme.getId());
            scheme.setIssueTypes(modelMapper.map(issueTypes, new TypeToken<List<IssueTypeDTO>>() {
            }.getType()));
        }
        Page<IssueTypeSchemeDTO> pagesDTO = new Page<>();
        pagesDTO.setNumber(pages.getNumber());
        pagesDTO.setNumberOfElements(pages.getNumberOfElements());
        pagesDTO.setSize(pages.getSize());
        pagesDTO.setTotalElements(pages.getTotalElements());
        pagesDTO.setTotalPages(pages.getTotalPages());
        pagesDTO.setContent(contentDTO);
        return pagesDTO;
    }

    @Override
    public Boolean checkName(Long organizationId, String name, Long id) {
        IssueTypeScheme select = new IssueTypeScheme();
        select.setName(name);
        select.setOrganizationId(organizationId);
        select = issueTypeSchemeMapper.selectOne(select);
        if (select != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验返回false
            return select.getId().equals(id);
        }
        return true;
    }

    @Override
    public void createConfig(Long organizationId, Long issueTypeSchemeId, List<IssueTypeDTO> issueTypeDTOS) {
        if (issueTypeDTOS != null && !issueTypeDTOS.isEmpty()) {
            int sequence = 0;
            for (IssueTypeDTO issueType : issueTypeDTOS) {
                if (issueTypeMapper.selectByPrimaryKey(issueType.getId()) != null) {
                    IssueTypeSchemeConfig config = new IssueTypeSchemeConfig();
                    config.setIssueTypeId(issueType.getId());
                    config.setOrganizationId(organizationId);
                    config.setSchemeId(issueTypeSchemeId);
                    config.setSequence(BigDecimal.valueOf(sequence));
                    issueTypeSchemeConfigMapper.insert(config);
                } else {
                    throw new CommonException("error.issueType.notFound");
                }
                sequence++;
            }
        } else {
            throw new CommonException("error.issueType.null");
        }
    }

    @Override
    public IssueTypeSchemeDTO queryByProjectId(Long projectId) {
        ProjectConfig projectConfig = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.ISSUE_TYPE, SchemeApplyType.CLOOPM);

        if (projectConfig.getSchemeId() == null) {
            throw new CommonException("error.projectConfig.schemeId.null");
        }
        return queryById(projectUtil.getOrganizationId(projectId), projectConfig.getSchemeId());
    }

    @Override
    public void initByConsumeCreateProject(Long projectId, String projectCode) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        IssueType query = new IssueType();
        query.setOrganizationId(organizationId);
        query.setInitialize(true);
        List<IssueType> issueTypes = issueTypeMapper.select(query);
        //处理老的组织没有创建的数据
        issueTypes = initOrganizationIssueType(organizationId, issueTypes);
        Map<String, IssueType> issueTypeMap = issueTypes.stream().collect(Collectors.toMap(IssueType::getTypeCode, x -> x));
        //初始化敏捷问题类型方案
        initScheme(projectId, organizationId, projectCode + "默认类型方案【敏捷】", issueTypeMap.get(InitIssueType.STORY.getTypeCode()).getId(), SchemeApplyType.AGILE, issueTypeMap);
        //初始化测试问题类型方案
        initScheme(projectId, organizationId, projectCode + "默认类型方案【测试】", issueTypeMap.get(InitIssueType.TEST.getTypeCode()).getId(), SchemeApplyType.TEST, issueTypeMap);
    }

    private List<IssueType> initOrganizationIssueType(Long organizationId, List<IssueType> issueTypes) {
        if (issueTypes == null || issueTypes.isEmpty()) {
            //注册组织初始化六种问题类型
            issueTypeService.initIssueTypeByConsumeCreateOrganization(organizationId);
            //注册组织初始化优先级
            priorityService.initProrityByOrganization(Collections.singletonList(organizationId));
            IssueType query = new IssueType();
            query.setOrganizationId(organizationId);
            query.setInitialize(true);
            return issueTypeMapper.select(query);
        } else {
            return issueTypes;
        }
    }

    /**
     * 初始化方案
     *
     * @param projectId
     * @param organizationId
     * @param name
     * @param defaultIssueTypeId
     * @param schemeApplyType
     * @param issueTypeMap
     */
    private void initScheme(Long projectId, Long organizationId, String name, Long defaultIssueTypeId, String schemeApplyType, Map<String, IssueType> issueTypeMap) {
        //初始化敏捷问题类型方案
        IssueTypeScheme issueTypeScheme = new IssueTypeScheme();
        issueTypeScheme.setName(name);
        issueTypeScheme.setDefaultIssueTypeId(defaultIssueTypeId);
        issueTypeScheme.setApplyType(schemeApplyType);
        issueTypeScheme.setOrganizationId(organizationId);
        issueTypeScheme.setDescription(name);
        //保证幂等性
        List<IssueTypeScheme> issueTypeSchemes = issueTypeSchemeMapper.select(issueTypeScheme);
        if (issueTypeSchemes.isEmpty()) {
            if (issueTypeSchemeMapper.insert(issueTypeScheme) != 1) {
                throw new CommonException("error.issueTypeScheme.create");
            }
            Integer sequence = 0;
            for (InitIssueType initIssueType : InitIssueType.listByApplyType(schemeApplyType)) {
                sequence++;
                IssueType issueType = issueTypeMap.get(initIssueType.getTypeCode());
                IssueTypeSchemeConfig schemeConfig = new IssueTypeSchemeConfig(issueTypeScheme.getId(), issueType.getId(), organizationId, BigDecimal.valueOf(sequence));
                if (issueTypeSchemeConfigMapper.insert(schemeConfig) != 1) {
                    throw new CommonException("error.issueTypeSchemeConfig.create");
                }
            }
            //创建与项目的关联关系
            projectConfigService.create(projectId, issueTypeScheme.getId(), SchemeType.ISSUE_TYPE, schemeApplyType);
        }
    }
}
