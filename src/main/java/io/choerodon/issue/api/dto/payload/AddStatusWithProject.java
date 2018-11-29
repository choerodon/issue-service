package io.choerodon.issue.api.dto.payload;

import io.choerodon.issue.infra.feign.dto.StatusDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2018/11/28
 */
public class AddStatusWithProject {
    private Long projectId;
    private List<Long> addStatusIds;
    private List<StatusDTO> addStatuses;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<Long> getAddStatusIds() {
        return addStatusIds;
    }

    public void setAddStatusIds(List<Long> addStatusIds) {
        this.addStatusIds = addStatusIds;
    }

    public List<StatusDTO> getAddStatuses() {
        return addStatuses;
    }

    public void setAddStatuses(List<StatusDTO> addStatuses) {
        this.addStatuses = addStatuses;
    }
}
