package io.choerodon.issue;

import io.choerodon.eureka.event.EurekaEventHandler;
import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/9/25.
 * Email: fuqianghuang01@gmail.com
 */
@SpringBootApplication
@EnableEurekaClient
@EnableChoerodonResourceServer
public class IssueServiceApplication {

    public static void main(String[] args) {
        //此处执行Eureka服务发现的初始化
        EurekaEventHandler.getInstance().init();
        SpringApplication.run(IssueServiceApplication.class, args);
    }

}
