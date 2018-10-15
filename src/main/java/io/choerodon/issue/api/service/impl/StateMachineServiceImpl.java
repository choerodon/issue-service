package io.choerodon.issue.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.api.service.IssueService;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.api.service.StateMachineService;
import io.choerodon.issue.domain.Issue;
import io.choerodon.issue.infra.enums.CloopmCommonString;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.TransformInfo;
import io.choerodon.issue.infra.mapper.IssueMapper;
import io.choerodon.issue.statemachine.annotation.Condition;
import io.choerodon.issue.statemachine.annotation.Postpostition;
import io.choerodon.issue.statemachine.annotation.UpdateStatus;
import io.choerodon.issue.statemachine.annotation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public class StateMachineServiceImpl implements StateMachineService {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineServiceImpl.class);

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
    private AnalyzeServiceManager analyzeServiceManager;

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
    public ResponseEntity<List<TransformInfo>> transfList(Long organizationId, Long projectId, Long issueId) {
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
        return stateMachineClient.executeTransform(organizationId, serverCode, stateMachineId, issueId,
                issue.getStatusId(), transfId);
    }

    @Condition(code = "just_reporter", name = "仅允许报告人", description = "只有该报告人才能执行转换")
    public Boolean justReporter(Long instanceId, StateMachineConfigDTO configDTO) {
        logger.info("执行条件：justReporter, instanceId:{},configDTO:{}", instanceId, configDTO);
        //测试两种异常：
//        ExecuteResult xx=null;
//        if (true) {
//            //获取值时空指针异常
////            xx.getIsSuccess();
//            //手动抛出的异常
//            throw new RuntimeException("test");
//        }
//        Issue issue = issueService.selectByPrimaryKey(instanceId);
//        Long reporterId = DetailsHelper.getUserDetails().getUserId();
//        return issue != null && issue.getReporterId() != null && issue.getReporterId().equals(reporterId);
        return true;
    }

    @Validator(code = "permission_validator", name = "权限校验", description = "校验操作的用户权限")
    public Boolean permissionValidator(Long instanceId, StateMachineConfigDTO configDTO) {
        logger.info("执行验证：permissionValidator, instanceId:{},configDTO:{}", instanceId, configDTO);
        return true;
    }

    @Postpostition(code = "assign_current_user", name = "分派给当前用户", description = "分派给当前用户")
    public Boolean assignCurrentUser(Long instanceId, StateMachineConfigDTO configDTO) {
        logger.info("执行后置动作：assignCurrentUser, instanceId:{},configDTO:{}", instanceId, configDTO);
        return true;
    }

    @UpdateStatus
    public void updateStatus(Long instanceId, Long targetStatusId) {
        logger.info("执行状态更新：updateStatus, instanceId:{},targetStatusId:{}", instanceId, targetStatusId);
//        Issue issue = issueService.selectByPrimaryKey(instanceId);
//        if (targetStatusId == null) {
//            throw new CommonException("error.updateStatus.targetStateId.null");
//        }
//        issue.setStatusId(targetStatusId);
//        int isUpdate = issueService.updateOptional(issue, "statusId");
//        if (isUpdate != 1) {
//            throw new CommonException("error.updateStatus.updateIssueState");
//        }
    }
}
