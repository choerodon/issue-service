package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.service.AnalyzeIssueRecordService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取解析类
 *
 * @author peng.jiang@hand-china.com
 **/

@Component
public class AnalyzeServiceManager implements ApplicationListener {

    private List<AnalyzeIssueRecordService> analyzeServices = new ArrayList<>();

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
            Map<String, AnalyzeIssueRecordService> analyzeServicesBeanMap = applicationContext.getBeansOfType(AnalyzeIssueRecordService.class);
            analyzeServicesBeanMap.forEach((k, v) -> {
                analyzeServices.add(v);
            });
        }
    }

    public List<AnalyzeIssueRecordService> getAnalyzeServices() {
        return analyzeServices;
    }
}
