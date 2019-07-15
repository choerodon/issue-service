package io.choerodon.issue.api.vo.payload;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/26.
 * Email: fuqianghuang01@gmail.com
 */
public class StatusPayload {

    private Long statusId;

    private String statusName;

    private String type;

    private Long projectId;

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
