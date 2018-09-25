package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.IssueReplyDTO;
import io.choerodon.issue.api.service.IssueRecordService;
import io.choerodon.issue.api.service.IssueReplyService;
import io.choerodon.issue.domain.IssueRecord;
import io.choerodon.issue.domain.IssueReply;
import io.choerodon.issue.infra.enums.IssueRecordEnums;
import io.choerodon.issue.infra.mapper.IssueReplyMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @date 2018/9/3
 */
@Component
@RefreshScope
public class IssueReplyServiceImpl extends BaseServiceImpl<IssueReply> implements IssueReplyService {
    @Autowired
    private IssueReplyMapper issueReplyMapper;

    @Autowired
    private IssueRecordService issueRecordService;


    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<IssueReplyDTO> listQuery(IssueReplyDTO issueReplyDTO, String params) {
        IssueReply issueReply = modelMapper.map(issueReplyDTO, IssueReply.class);
        List<IssueReply> issueReplyList = issueReplyMapper.fulltextSearch(issueReply, params);
        return modelMapper.map(issueReplyList, new TypeToken<List<IssueReplyDTO>>() {
        }.getType());
    }

    @Override
    public IssueReplyDTO create(Long projectId, IssueReplyDTO issueReplyDTO) {
        issueReplyDTO.setProjectId(projectId);
        IssueReply issueReply = modelMapper.map(issueReplyDTO, IssueReply.class);
        if (issueReplyMapper.insert(issueReply) != 1) {
            throw new CommonException("error.issueReply.create");
        }

        issueReply = issueReplyMapper.selectByPrimaryKey(issueReply.getId());
        createIssueRecord(issueReply, IssueRecordEnums.ReplyAction.REPLY.code(), IssueRecordEnums.ReplyAction.REPLY.value(),
                null, null, issueReply.getId(), null);
        return modelMapper.map(issueReply, IssueReplyDTO.class);
    }

    @Override
    public IssueReplyDTO update(IssueReplyDTO issueReplyDTO) {
        IssueReply issueReply = modelMapper.map(issueReplyDTO, IssueReply.class);
        IssueReply reply = issueReplyMapper.selectByPrimaryKey(issueReplyDTO.getId());
        String oldValue=reply.getContent();
        int isUpdate = issueReplyMapper.updateByPrimaryKey(issueReply);
        if (isUpdate != 1) {
            throw new CommonException("error.issueReply.update");
        }
        IssueReply issueRecord=issueReplyMapper.selectByPrimaryKey(issueReplyDTO.getId());
        createIssueRecord(issueReply,IssueRecordEnums.ReplyAction.UPDATE_REPLY.code(),IssueRecordEnums.ReplyAction.UPDATE_REPLY.value(),
                null,oldValue,null,issueReply.getContent());
        return queryByIssueReplyId(issueReplyDTO.getProjectId(), issueReplyDTO.getId());
    }

    @Override
    public IssueReplyDTO queryByIssueReplyId(Long projectId, Long id) {
        IssueReply issueReply = issueReplyMapper.selectByPrimaryKey(id);
        if (issueReply == null) {
            throw new CommonException("error.base.notFound");
        }
        IssueReplyDTO issueReplyDTO = modelMapper.map(issueReply, IssueReplyDTO.class);
        return issueReplyDTO;
    }

    @Override
    public Boolean delete(Long projectId, Long issueReplyId) {
        Map<String, Object> result = checkDelete(projectId, issueReplyId);
        IssueReply reply = issueReplyMapper.selectByPrimaryKey(issueReplyId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = issueReplyMapper.deleteByPrimaryKey(issueReplyId);
            if (isDelete != 1) {
                throw new CommonException("error.state.delete");
            }
        } else {
            return false;
        }

        createIssueRecord(reply,IssueRecordEnums.ReplyAction.DELETE_REPLY.code(),IssueRecordEnums.ReplyAction.DELETE_REPLY.value(),
                issueReplyId,null,null,null);

        return true;
    }

    @Override
    public Map<String, Object> checkDelete(Long projectId, Long issueReplyId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        IssueReply issueReply = issueReplyMapper.selectByPrimaryKey(issueReplyId);
        if (issueReply == null) {
            throw new CommonException("error.base.notFound");
        } else if (!issueReply.getProjectId().equals(projectId)) {
            throw new CommonException("error.issueReply.illegal");
        }
        return result;
    }

    public void createIssueRecord(IssueReply issueReply, String fieldSource, String fieldName,
                                  Long oldId, String oldValue, Long newId, String newValue) {
        IssueRecord issueRecord = new IssueRecord();
        issueRecord.setIssueId(issueReply.getIssueId());
        issueRecord.setFieldSource(fieldSource);
        issueRecord.setFieldName(fieldName);
        issueRecord.setNewId(newId);
        issueRecord.setNewValue(newValue);
        issueRecord.setOldValue(oldValue);
        issueRecord.setOldId(oldId);
        issueRecord.setProjectId(issueReply.getProjectId());
        issueRecordService.create(issueReply.getProjectId(), issueReply.getIssueId(), issueRecord);
    }
}

