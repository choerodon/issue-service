package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.api.service.IssueService;
import io.choerodon.issue.api.service.StateMachineConfigService;
import io.choerodon.issue.domain.Issue;
import io.choerodon.issue.infra.enums.StateMachineConfigEnums;
import io.choerodon.issue.infra.enums.StateMachineConfigType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
@Service
public class StateMachinePostpositionService implements StateMachineConfigService {

    @Autowired
    private IssueService issueService;

    @Override
    public String configType() {
        return StateMachineConfigType.POSTPOSITION;
    }

    /**
     * 后置处理执行 入口
     *
     * @param instanceId
     * @param configDTOS
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean configExecute(Long instanceId, Long targetStateId, List<StateMachineConfigDTO> configDTOS) {
        Issue issue = issueService.selectByPrimaryKey(instanceId);
        if(targetStateId==null){
            throw new CommonException("error.configExecute.targetStateId.null");
        }
        issue.setStatusId(targetStateId);
        int isUpdate = issueService.updateOptional(issue,"statusId");
        if (isUpdate != 1){
            throw new CommonException("error.configExecute.updateIssueState");
        }
        for (StateMachineConfigDTO configDTO : configDTOS) {
            switch (configDTO.getCode()) {
                case StateMachineConfigEnums.POSTPOSITION_ASSIGN_CURRENTUSER:
                    assignCurrentUser(instanceId);
                    break;
                case StateMachineConfigEnums.POSTPOSITION_ASSIGN_REPORTER:
                    assignReporter(instanceId);
                    break;
                case StateMachineConfigEnums.POSTPOSITION_ASSIGN_DEVELOPER:
                    assignDeveloper(instanceId);
                    break;
                default:
                    throw new CommonException("error.configDTO.postposition.noMatch");
            }
        }
        return true;
    }

    /**
     * assignCurrentUser 分配给当前用户
     * @param instanceId
     */
    private void assignCurrentUser(Long instanceId){
        Long currentUserId = DetailsHelper.getUserDetails().getUserId();
        Issue issue = issueService.selectByPrimaryKey(instanceId);
        issue.setHandlerId(currentUserId);
        int isUpdate = issueService.updateOptional(issue,"handlerId");
        if (isUpdate != 1){
            throw new CommonException("error.configDTO.assignCurrentUser");
        }
    }

    /**
     * assignReporter 分配给报告人
     * @param instanceId
     */
    private void assignReporter(Long instanceId){
        Issue issue = issueService.selectByPrimaryKey(instanceId);
        issue.setHandlerId(issue.getReporterId());
        int isUpdate = issueService.updateOptional(issue,"handlerId");
        if (isUpdate != 1){
            throw new CommonException("error.configDTO.assignCurrentUser");
        }
    }

    /**
     * assignDeveloper 分配给负责开发人
     * @param instanceId
     */
    private void assignDeveloper(Long instanceId){
        //todo 实现
    }
}
