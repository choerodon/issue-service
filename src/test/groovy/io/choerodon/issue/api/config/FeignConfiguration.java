package io.choerodon.issue.api.config;

import feign.Contract;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class FeignConfiguration {

    @Bean
    public Contract feignContract() {

        return new Contract.Default();
    }

}