package io.choerodon.issue.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.api.service.IssueService;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.api.service.StateMachineService;
import io.choerodon.issue.domain.Issue;
import io.choerodon.issue.domain.IssueRecord;
import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.issue.domain.StateMachineSchemeConfig;
import io.choerodon.issue.infra.enums.CloopmCommonString;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.TransformDTO;
import io.choerodon.issue.infra.mapper.IssueMapper;
import io.choerodon.issue.infra.mapper.IssueRecordMapper;
import io.choerodon.issue.infra.mapper.StateMachineSchemeConfigMapper;
import io.choerodon.issue.infra.mapper.StateMachineSchemeMapper;
import io.choerodon.issue.statemachine.annotation.Condition;
import io.choerodon.issue.statemachine.annotation.Postpostition;
import io.choerodon.issue.statemachine.annotation.UpdateStatus;
import io.choerodon.issue.statemachine.annotation.Validator;
import io.choerodon.issue.statemachine.fegin.InstanceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author shinan.chen
 * @date 2018/9/25
 */
@Component
public class StateMachineServiceImpl implements StateMachineService {

    @Value("${spring.application.name:default}")
    private String serverCode;
    @Autowired
    private StateMachineFeignClient stateMachineClient;
    @Autowired
    private StateMachineSchemeService schemeService;
    @Autowired
    private IssueService issueService;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private IssueRecordMapper issueRecordMapper;
    @Autowired
    private StateMachineSchemeConfigMapper stateMachineSchemeConfigMapper;
    @Autowired
    private StateMachineSchemeMapper stateMachineSchemeMapper;
    @Autowired
    private AnalyzeServiceManager analyzeServiceManager;
    @Autowired
    private InstanceFeignClient instanceFeignClient;

    @Override
    public ResponseEntity<Page<StateMachineDTO>> pageQuery(Long organizationId, Integer page, Integer size, String[] sort, String name, String description, String[] param) {
        ResponseEntity<Page<StateMachineDTO>> responseEntity = stateMachineClient.pagingQuery(organizationId, page, size, sort, name, description, param);
        if (responseEntity != null && responseEntity.getBody() != null && responseEntity.getBody().getContent() != null) {
            for (StateMachineDTO stateMachineDTO : responseEntity.getBody().getContent()) {
                List<StateMachineSchemeDTO> list = schemeService.querySchemeByStateMachineId(organizationId, stateMachineDTO.getId());
                //列表去重
                List<StateMachineSchemeDTO> unique = list.stream().collect(
                        collectingAndThen(
                                toCollection(() -> new TreeSet<>(comparingLong(StateMachineSchemeDTO::getId))), ArrayList::new)
                );
                stateMachineDTO.setStateMachineSchemeDTOs(unique);
            }
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<Boolean> delete(Long organizationId, Long stateMachineId) {
        List<StateMachineSchemeDTO> list = schemeService.querySchemeByStateMachineId(organizationId, stateMachineId);
        if (list != null && !list.isEmpty()) {
            throw new CommonException("error.stateMachine.delete");
        }
        ResponseEntity<StateMachineDTO> responseEntity = stateMachineClient.queryStateMachineById(organizationId, stateMachineId);
        if (responseEntity == null || responseEntity.getBody() == null) {
            throw new CommonException("error.stateMachine.delete.noFound");
        }
        return stateMachineClient.delete(organizationId, stateMachineId);
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long stateMachineId) {
        Map<String, Object> map = new HashMap<>();
        ResponseEntity<StateMachineDTO> responseEntity = stateMachineClient.queryStateMachineById(organizationId, stateMachineId);
        if (responseEntity == null || responseEntity.getBody() == null) {
            map.put(CloopmCommonString.CAN_DELETE, false);
            map.put("reason", "noFound");
            return map;
        }
        List<StateMachineSchemeDTO> list = schemeService.querySchemeByStateMachineId(organizationId, stateMachineId);
        if (list == null || list.isEmpty()) {
            map.put(CloopmCommonString.CAN_DELETE, true);
        } else {
            map.put(CloopmCommonString.CAN_DELETE, false);
            map.put("schemeUsed", list.size());
        }
        return map;
    }

    @Override
    public ResponseEntity<List<TransformDTO>> transfList(Long organizationId, Long projectId, Long issueId) {
        Long stateMachineId = issueService.getStateMachineId(projectId, issueId);
        Long currentStateId = issueMapper.selectByPrimaryKey(issueId).getStatusId();
        return stateMachineClient.transformList(organizationId, serverCode,
                stateMachineId, issueId, currentStateId);
    }

    @Override
    public ResponseEntity<ExecuteResult> doTransf(Long organizationId, Long projectId, Long issueId, Long transfId) {
        Long stateMachineId = issueService.getStateMachineId(projectId, issueId);
        Issue issue = issueService.selectByPrimaryKey(issueId);
        if (issue == null) {
            throw new CommonException("error.issue.noFound");
        }
        return instanceFeignClient.executeTransform(organizationId, serverCode, stateMachineId, issueId,
                issue.getStatusId(), transfId);
    }

    @Override
    public Long queryBySchemeIdAndIssueTypeId(Long stateMachineSchemeId, Long issueTypeId) {
        StateMachineSchemeConfig config = new StateMachineSchemeConfig();
        config.setSchemeId(stateMachineSchemeId);
        config.setIssueTypeId(issueTypeId);
        Long stateMachineId;
        List<StateMachineSchemeConfig> configs = stateMachineSchemeConfigMapper.select(config);
        if (configs.isEmpty()) {
            StateMachineScheme stateMachineScheme = stateMachineSchemeMapper.selectByPrimaryKey(stateMachineSchemeId);
            if (stateMachineScheme == null) {
                throw new CommonException("error.queryBySchemeIdAndIssueTypeId.stateMachineScheme.notFound");
            }
            if (stateMachineScheme.getDefaultStateMachineId() != null) {
                stateMachineId = stateMachineScheme.getDefaultStateMachineId();
            } else {
                throw new CommonException("error.queryBySchemeIdAndIssueTypeId.defaultStateMachineId.null");
            }
        } else {
            stateMachineId = configs.get(0).getStateMachineId();
        }
        return stateMachineId;
    }

    @Condition(code = "just_reporter", name = "仅允许报告人", description = "只有该报告人才能执行转换")
    public Boolean justReporter(Long instanceId, StateMachineConfigDTO configDTO) {
        Issue issue = issueMapper.selectByPrimaryKey(instanceId);
        Long currentUserId = DetailsHelper.getUserDetails().getUserId();
        return issue != null && issue.getReporterId() != null && issue.getReporterId().equals(currentUserId);
    }

    @Condition(code = "just_admin", name = "仅允许管理员", description = "只有该管理员才能执行转换")
    public Boolean justAdmin(Long instanceId, StateMachineConfigDTO configDTO) {
        return true;
    }

    @Validator(code = "permission_validator", name = "权限校验", description = "校验操作的用户权限")
    public Boolean permissionValidator(Long instanceId, StateMachineConfigDTO configDTO) {
        return true;
    }

    @Validator(code = "time_validator", name = "时间校验", description = "根据时间校验权限")
    public Boolean timeValidator(Long instanceId, StateMachineConfigDTO configDTO) {
//        throw new CommonException("xx");
        return true;
    }

    @Postpostition(code = "assign_current_user", name = "分派给当前用户", description = "分派给当前用户")
    public void assignCurrentUser(Long instanceId, StateMachineConfigDTO configDTO) {
//        //测试两种异常：
//        ExecuteResult xx=null;
//        if (true) {
//            //获取值时空指针异常
////            xx.getSuccess();
//            //手动抛出的异常
//            throw new RuntimeException("test11");
//        }
        Long currentUserId = DetailsHelper.getUserDetails().getUserId();
        Issue issue = issueMapper.selectByPrimaryKey(instanceId);
        issue.setHandlerId(currentUserId);
        int update = issueService.updateOptional(issue, "handlerId");
        if (update != 1) {
            throw new CommonException("error.assignCurrentUser.updateOptional");
        }
    }

    @Postpostition(code = "create_change_log", name = "创建日志", description = "创建日志")
    public void createChangeLog(Long instanceId, StateMachineConfigDTO configDTO) {
//        Issue issue = issueMapper.selectByPrimaryKey(instanceId);
//        issue.setDescription(issue.getDescription()+"1");
//        issueService.updateOptional(issue,"description");

        IssueRecord issueRecord = new IssueRecord();
        issueRecord.setIssueId(instanceId);
        issueRecordMapper.insert(issueRecord);

        throw new CommonException("xa");
    }

    @UpdateStatus
    public void updateStatus(Long instanceId, Long targetStatusId) {
        Issue issue = issueMapper.selectByPrimaryKey(instanceId);
        if (targetStatusId == null) {
            throw new CommonException("error.updateStatus.targetStateId.null");
        }
        issue.setStatusId(targetStatusId);
        int update = issueService.updateOptional(issue, "statusId");
        if (update != 1) {
            throw new CommonException("error.updateStatus.updateOptional");
        }
        issue = issueMapper.selectByPrimaryKey(instanceId);
        issue.setDescription(issue.getDescription() + "1");
        issueService.updateOptional(issue, "description");
//        throw new CommonException("error.updateStatus.updateOptional");
    }

    @Override
    public Issue createIssue(Long organizationId, Long stateMachineId) {
        Issue issue = new Issue();
        issue.setDescription("10.18测试问题");
        issueMapper.insert(issue);

        ResponseEntity<ExecuteResult> executeResult = instanceFeignClient.startInstance(organizationId, serverCode, stateMachineId, issue.getId());
        //feign调用执行失败，抛出异常回滚
        if (!executeResult.getBody().getSuccess()) {
            //手动回滚数据
            issueMapper.deleteByPrimaryKey(issue.getId());
            throw new CommonException(executeResult.getBody().getErrorMessage());
        }
        return issueMapper.selectByPrimaryKey(issue.getId());
    }
}
