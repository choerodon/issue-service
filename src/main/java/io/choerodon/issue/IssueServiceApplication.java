package io.choerodon.issue;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/25.
 * Email: fuqianghuang01@gmail.com
 */
@SpringBootApplication
@EnableFeignClients("io.choerodon")
@EnableEurekaClient
//@EnableChoerodonResourceServer
public class IssueServiceApplication {

    public static void main(String[] args){
        SpringApplication.run(IssueServiceApplication.class, args);
    }

}
