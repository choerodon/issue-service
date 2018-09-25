package io.choerodon.issue.infra.feign.dto;

/**
 * @author shinan.chen
 * @date 2018/9/10
 */
public class UserInfo {
    private Long userId;
    private String realName;
    private String email;
    private String imageUrl;

    public UserInfo(Long userId, String realName, String email, String imageUrl) {
        this.userId = userId;
        this.realName = realName;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
