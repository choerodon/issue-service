package io.choerodon.issue.infra.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * 如果是单元测试的配置，则开启testScanIgnore，可以mockFeignClient
 * 正常项目运行，启用FeignClient
 *
 * @author dinghuang123@gmail.com
 * @since 2018/12/11
 */
@Configuration
@ConditionalOnProperty(prefix = "testScanIgnore", name = "enabled", havingValue = "false", matchIfMissing = true)
@EnableFeignClients("io.choerodon")
public class FeignConfigure {
}