package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.IssueRecordViewDTO;
import io.choerodon.issue.api.dto.IssueReplyDTO;
import io.choerodon.issue.api.dto.UserDTO;
import io.choerodon.issue.api.service.AnalyzeIssueRecordService;
import io.choerodon.issue.api.service.IssueReplyService;
import io.choerodon.issue.domain.IssueRecord;
import io.choerodon.issue.domain.IssueReply;
import io.choerodon.issue.infra.enums.IssueRecordEnums;
import io.choerodon.issue.infra.feign.UserFeignClient;
import org.modelmapper.ModelMapper;
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
public class AnalyzeReplyRecordServiceImpl implements AnalyzeIssueRecordService {

    @Autowired
    private IssueReplyService replyService;
    @Autowired
    private UserFeignClient iamServiceFeign;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public String recordType() {
        return IssueRecordEnums.FieldSource.REPLY.value();
    }

    @Override
    public IssueRecordViewDTO analyzeIssueRecord(Long projectId, IssueRecord issueRecord) {
        IssueRecordViewDTO viewDTO = transfBaseField(issueRecord);
        ResponseEntity<UserDTO> userDTOResponseEntity = iamServiceFeign.queryInfo(issueRecord.getCreatedBy());
        if (userDTOResponseEntity != null && userDTOResponseEntity.getBody() != null){
            viewDTO.setOperatorName(userDTOResponseEntity.getBody().getLoginName());
            viewDTO.setImageUrl(userDTOResponseEntity.getBody().getImageUrl());
        }
        String operationType = operationType(issueRecord);
        if (IssueRecordEnums.IssueRecordOperationType.CREATE.value().equals(operationType)) {
            viewDTO.setAction(IssueRecordEnums.ReplyAction.REPLY.value());
            analyzeCreate(viewDTO, issueRecord);
        } else if (IssueRecordEnums.IssueRecordOperationType.UPDATE.value().equals(operationType)) {
            viewDTO.setAction(IssueRecordEnums.ReplyAction.UPDATE_REPLY.value());
            viewDTO.setFieldSource(IssueRecordEnums.FieldSource.SYSTEM.value());//修改回复，页面当做系统字段修改展示
            analyzeUpdate(viewDTO, issueRecord);
        } else if (IssueRecordEnums.IssueRecordOperationType.DELETE.value().equals(operationType)) {
            viewDTO.setAction(IssueRecordEnums.ReplyAction.DELETE_REPLY.value());
            analyzeUpdate(viewDTO, issueRecord);
        }
        return viewDTO;
    }

    @Override
    public void analyzeCreate(IssueRecordViewDTO viewDTO, IssueRecord issueRecord) {
        List<IssueReplyDTO> replyDTOS = new ArrayList<>();
        IssueReply issueReply = replyService.selectByPrimaryKey(issueRecord.getNewId());
        IssueReplyDTO issueReplyDTO = modelMapper.map(issueReply, IssueReplyDTO.class);
        if (issueReplyDTO.getSourceReplyId() == null) {
            replyDTOS.add(issueReplyDTO);
        } else {
            IssueReply sourceReply = replyService.selectByPrimaryKey(issueReplyDTO.getSourceReplyId());
            IssueReplyDTO sourceReplyDTO = modelMapper.map(sourceReply, IssueReplyDTO.class);
            replyDTOS.add(sourceReplyDTO);//源回复
            replyDTOS.add(issueReplyDTO);//回复
        }
        viewDTO.setReplyDTOS(replyDTOS);
    }

    @Override
    public void analyzeUpdate(IssueRecordViewDTO viewDTO, IssueRecord issueRecord) {
        List<IssueReplyDTO> replyDTOS = new ArrayList<>();
        IssueReply issueReply = replyService.selectByPrimaryKey(issueRecord.getNewId());
        IssueReplyDTO issueReplyDTO = modelMapper.map(issueReply, IssueReplyDTO.class);

        IssueReplyDTO oldIssueReplyDTO = new IssueReplyDTO();
        oldIssueReplyDTO.setContent(issueRecord.getOldValue());
        replyDTOS.add(oldIssueReplyDTO);//修改前回复
        replyDTOS.add(issueReplyDTO);//修改后回复
        viewDTO.setReplyDTOS(replyDTOS);
    }

    @Override
    public void analyzeDelete(IssueRecordViewDTO viewDTO, IssueRecord issueRecord) {
        List<IssueReplyDTO> replyDTOS = new ArrayList<>();
        IssueReplyDTO issueReplyDTO = new IssueReplyDTO();
        issueReplyDTO.setContent(issueRecord.getOldValue());
        replyDTOS.add(issueReplyDTO);//删除的回复
        viewDTO.setReplyDTOS(replyDTOS);
    }
}
