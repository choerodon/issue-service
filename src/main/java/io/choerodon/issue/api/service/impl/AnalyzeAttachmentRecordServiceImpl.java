package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.AttachmentDTO;
import io.choerodon.issue.api.dto.IssueRecordViewDTO;
import io.choerodon.issue.api.dto.UserDTO;
import io.choerodon.issue.api.service.AnalyzeIssueRecordService;
import io.choerodon.issue.api.service.AttachmentService;
import io.choerodon.issue.domain.Attachment;
import io.choerodon.issue.domain.IssueRecord;
import io.choerodon.issue.infra.enums.IssueRecordEnums;
import io.choerodon.issue.infra.feign.UserFeignClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author peng.jiang
 * @Date 2018/9/4
 */
@Component
public class AnalyzeAttachmentRecordServiceImpl implements AnalyzeIssueRecordService {

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    private UserFeignClient iamServiceFeign;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public String recordType() {
        return IssueRecordEnums.FieldSource.ATTACHMENT.value();
    }

    public IssueRecordViewDTO analyzeIssueRecord(Long projectId, IssueRecord issueRecord) {
        IssueRecordViewDTO viewDTO = transfBaseField(issueRecord);
        ResponseEntity<UserDTO> userDTOResponseEntity = iamServiceFeign.queryInfo(issueRecord.getCreatedBy());
        if (userDTOResponseEntity != null && userDTOResponseEntity.getBody() != null){
            viewDTO.setOperatorName(userDTOResponseEntity.getBody().getLoginName());
            viewDTO.setImageUrl(userDTOResponseEntity.getBody().getImageUrl());
        }
        String operationType = operationType(issueRecord);
        if (IssueRecordEnums.IssueRecordOperationType.CREATE.value().equals(operationType)) {
            viewDTO.setAction(IssueRecordEnums.AttachmentAction.UPLOAD_ATTACHMENT.value());
            analyzeCreate(viewDTO, issueRecord);
        } else if (IssueRecordEnums.IssueRecordOperationType.DELETE.value().equals(operationType)) {
            viewDTO.setAction(IssueRecordEnums.AttachmentAction.DELETE_ATTACHMENT.value());
            analyzeDelete(viewDTO, issueRecord);
        }
        return viewDTO;
    }

    @Override
    public void analyzeCreate(IssueRecordViewDTO viewDTO, IssueRecord issueRecord) {
        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = attachmentService.selectByPrimaryKey(issueRecord.getNewId());
        attachments.add(attachment);
        if (issueRecord.getIssueRecords() != null && !issueRecord.getIssueRecords().isEmpty()) {
            for (IssueRecord temp : issueRecord.getIssueRecords()) {
                Attachment attachmentGroup = attachmentService.selectByPrimaryKey(temp.getNewId());
                attachments.add(attachmentGroup);
            }
        }
        List<AttachmentDTO> attachmentDTOS = modelMapper.map(attachments, new TypeToken<List<AttachmentDTO>>() {
        }.getType());
        viewDTO.setAttachmentDTOS(attachmentDTOS);
    }

    @Override
    public void analyzeDelete(IssueRecordViewDTO viewDTO, IssueRecord issueRecord) {
        List<AttachmentDTO> attachmentDTOS = new ArrayList<>();
        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setFileName(issueRecord.getOldValue());
        attachmentDTOS.add(attachmentDTO);
        if (issueRecord.getIssueRecords() != null && !issueRecord.getIssueRecords().isEmpty()) {
            for (IssueRecord temp : issueRecord.getIssueRecords()) {
                AttachmentDTO attachmentDTOGroup = new AttachmentDTO();
                attachmentDTOGroup.setFileName(temp.getOldValue());
                attachmentDTOS.add(attachmentDTOGroup);
            }
        }
        viewDTO.setAttachmentDTOS(attachmentDTOS);
    }
}
