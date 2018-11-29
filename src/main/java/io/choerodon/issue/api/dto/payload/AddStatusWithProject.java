package io.choerodon.issue.api.dto.payload;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2018/11/28
 */
public class AddStatusWithProject {
    private Long projectId;
    private List<Long> addStatusIds;

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
}
