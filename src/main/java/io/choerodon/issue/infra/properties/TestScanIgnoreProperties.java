package io.choerodon.issue.infra.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/12/11
 */
@ConfigurationProperties("testScanIgnore")
public class TestScanIgnoreProperties {

    private Boolean enabled = false;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
