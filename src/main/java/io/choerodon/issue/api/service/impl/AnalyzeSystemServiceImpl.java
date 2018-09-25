package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.IssueRecordViewDTO;
import io.choerodon.issue.api.dto.UserDTO;
import io.choerodon.issue.api.service.AnalyzeIssueRecordService;
import io.choerodon.issue.api.service.PriorityService;
import io.choerodon.issue.domain.Issue;
import io.choerodon.issue.domain.IssueRecord;
import io.choerodon.issue.domain.Priority;
import io.choerodon.issue.infra.enums.IssueRecordEnums;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.UserFeignClient;
import io.choerodon.issue.infra.feign.dto.StateDTO;
import io.choerodon.issue.infra.utils.ProjectUtil;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author peng.jiang
 * @Date 2018/9/4
 */
@Component
public class AnalyzeSystemServiceImpl implements AnalyzeIssueRecordService {
    @Autowired
    private PriorityService priorityService;
    @Autowired
    private UserFeignClient iamServiceFeign;
    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;
    @Autowired
    private ProjectUtil projectUtil;

    @Override
    public String recordType() {
        return IssueRecordEnums.FieldSource.SYSTEM.value();
    }

    @Override
    public IssueRecordViewDTO analyzeIssueRecord(Long projectId, IssueRecord issueRecord) {
        IssueRecordViewDTO viewDTO = transfBaseField(issueRecord);
        ResponseEntity<UserDTO> userDTOResponseEntity = iamServiceFeign.queryInfo(issueRecord.getCreatedBy());
        if (userDTOResponseEntity != null && userDTOResponseEntity.getBody() != null){
            viewDTO.setOperatorName(userDTOResponseEntity.getBody().getLoginName());
            viewDTO.setImageUrl(userDTOResponseEntity.getBody().getImageUrl());
        }
        switch (issueRecord.getFieldName()) {
            case Issue.FIELD_SUBJECT:
                viewDTO.setAction(IssueRecordEnums.IssueSystemAction.UPDATE_SUBJECT.value());
                viewDTO.setOldValue(issueRecord.getOldValue());
                viewDTO.setNewValue(issueRecord.getNewValue());
                break;
            case Issue.FIELD_DESCRIPTION:
                viewDTO.setAction(IssueRecordEnums.IssueSystemAction.UPDATE_DESCRIPTION.value());
                viewDTO.setOldValue(issueRecord.getOldValue());
                viewDTO.setNewValue(issueRecord.getNewValue());
                break;
            case Issue.FIELD_PRIORITY_ID:
                viewDTO.setAction(IssueRecordEnums.IssueSystemAction.UPDATE_PRIORITY_ID.value());
                Priority oldPriority = priorityService.selectByPrimaryKey(issueRecord.getOldId());
                Priority newPriority = priorityService.selectByPrimaryKey(issueRecord.getNewId());
                viewDTO.setOldValue(oldPriority.getName());
                viewDTO.setNewValue(newPriority.getName());
                break;
            case Issue.FIELD_STATUS_ID:
                viewDTO.setAction(IssueRecordEnums.IssueSystemAction.UPDATE_STATUS_ID.value());
                Long organizationId = projectUtil.getOrganizationId(projectId);
                ResponseEntity<StateDTO> newState = stateMachineFeignClient.getByStateId(organizationId,Long.valueOf(issueRecord.getNewValue()));
                if (newState != null && newState.getBody() != null){
                    viewDTO.setNewValue(newState.getBody().getName());
                }
                ResponseEntity<StateDTO> oldState = stateMachineFeignClient.getByStateId(organizationId,Long.valueOf(issueRecord.getOldValue()));
                if (oldState != null && oldState.getBody() != null){
                    viewDTO.setOldValue(oldState.getBody().getName());
                }
                break;
            case Issue.FIELD_HANDLER_ID:
                String operationType = operationType(issueRecord);
                if (IssueRecordEnums.IssueRecordOperationType.CREATE.value().equals(operationType)) {
                    //分派事件单
                    viewDTO.setAction(IssueRecordEnums.IssueSystemAction.CREATE_HANDLER_ID.value());
                    ResponseEntity<UserDTO> handlerResponseEntity = iamServiceFeign.queryInfo(Long.valueOf(issueRecord.getNewValue()));
                    if (handlerResponseEntity != null && handlerResponseEntity.getBody() != null){
                        viewDTO.setNewValue(handlerResponseEntity.getBody().getLoginName() + " " + handlerResponseEntity.getBody().getEmail());
                    }
                } else if (IssueRecordEnums.IssueRecordOperationType.UPDATE.value().equals(operationType)) {
                    //修改事件单处理人
                    viewDTO.setAction(IssueRecordEnums.IssueSystemAction.UPDATE_HANDLER_ID.value());
                    ResponseEntity<UserDTO> oldResponseEntity = iamServiceFeign.queryInfo(Long.valueOf(issueRecord.getOldValue()));
                    if (oldResponseEntity != null && oldResponseEntity.getBody() != null){
                        viewDTO.setOldValue(oldResponseEntity.getBody().getLoginName() + " " + oldResponseEntity.getBody().getEmail());
                    }
                    ResponseEntity<UserDTO> newResponseEntity = iamServiceFeign.queryInfo(Long.valueOf(issueRecord.getNewValue()));
                    if (newResponseEntity != null && newResponseEntity.getBody() != null){
                        viewDTO.setNewValue(newResponseEntity.getBody().getLoginName() + " " + newResponseEntity.getBody().getEmail());
                    }
                }
                break;
            default:
                throw new CommonException("error.issueRecord.system.noMatch");
        }
        return viewDTO;
    }
}
