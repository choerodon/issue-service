package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.AttachmentDTO;
import io.choerodon.issue.api.dto.IssueDTO;
import io.choerodon.issue.api.dto.SearchDTO;
import io.choerodon.issue.infra.feign.dto.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/9/10
 */
public interface UserService {

    /**
     * 根据id列表查询用户信息
     * @param userIds
     * @return
     */
    Map<Long, UserInfo> queryUserInfoMapByUserIds(List<Long> userIds);

    /**
     * 处理用户搜索，将name转为ids
     * @param projectId
     * @param searchDTO
     * @param nameKey
     * @param idsKey
     */
    void handleUserSearch(Long projectId, SearchDTO searchDTO, String nameKey, String idsKey);

    /**
     * 处理问题中的用户信息，将id映射为name
     * @param issueDTOS
     */
    public void handleUserInfo(List<IssueDTO> issueDTOS);

    /**
     * 处理附件中的用户信息，将id映射为name和avatar
     * @param attachmentDTOS
     */
    public void handleAttachmentUser(List<AttachmentDTO> attachmentDTOS);
}
