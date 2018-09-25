package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.IssueDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/9/20
 */
public interface StateService {

    /**
     * 处理问题的状态
     *
     * @param organizationId
     * @param issueDTOS
     */
    void handleState(Long organizationId, List<IssueDTO> issueDTOS);
}
