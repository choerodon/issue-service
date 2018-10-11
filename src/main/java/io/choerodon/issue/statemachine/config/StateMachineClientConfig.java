package io.choerodon.issue.statemachine.config;

import io.choerodon.issue.statemachine.bean.PropertyData;
import io.choerodon.issue.statemachine.endpoint.ClientEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shinan.chen
 * @date 2018/10/9
 */
@Configuration
public class StateMachineClientConfig {
    @Value("${spring.application.name}")
    private String serviceName;

    @Bean("stateMachinePropertyData")
    public PropertyData stateMachinePropertyData() {
        PropertyData stateMachinePropertyData = new PropertyData();
        stateMachinePropertyData.setServiceName(serviceName);
        return stateMachinePropertyData;
    }

    @Bean
    public ClientEndpoint clientEndpoint() {
        return new ClientEndpoint(stateMachinePropertyData());
    }
}
