package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.AttachmentDTO;
import io.choerodon.issue.domain.Attachment;
import io.choerodon.mybatis.service.BaseService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @date 2018/9/3
 */
public interface AttachmentService extends BaseService<Attachment> {
    List<AttachmentDTO> listQuery(AttachmentDTO attachmentDTO, String s);

    List<AttachmentDTO> queryByIssue(AttachmentDTO attachmentDTO, Long issueId);

    List<AttachmentDTO> create(Long projectId, AttachmentDTO attachmentDTO, HttpServletRequest request);

    Boolean delete(Long projectId, Long attachmentId);

    Map<String, Object> checkDelete(Long projectId, Long attachmentId);

    Attachment createAttachemnt(Attachment attachment);

    List<String> uploadForAddress(Long projectId, HttpServletRequest request);
}
