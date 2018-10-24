package io.choerodon.issue.api.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.issue.api.dto.payload.OrganizationEvent;
import io.choerodon.issue.api.dto.payload.ProjectEvent;
import io.choerodon.issue.api.service.*;
import io.choerodon.issue.domain.StateMachineScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @date 2018/9/10
 */
@Component
public class IssueEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IssueEventHandler.class);

    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private PriorityService priorityService;
    @Autowired
    private IssueTypeService issueTypeService;
    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;

    @Autowired
    private FieldService fieldService;


    private void loggerInfo(Object o) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("data: {}", o);
        }
    }

    /**
     * 创建项目事件
     *
     * @param data data
     */
    @SagaTask(code = "issue-init-project",
            description = "issue消费创建项目事件初始化项目数据",
            sagaCode = "iam-create-project",
            seq = 3)
    public String handleProjectInitByConsumeSagaTask(String data) {
        ProjectEvent projectEvent = JSONObject.parseObject(data, ProjectEvent.class);
        loggerInfo(projectEvent);

        //创建项目时创建初始化状态机方案
        StateMachineScheme stateMachineScheme = stateMachineSchemeService.createSchemeWithCreateProject(projectEvent.getProjectId(), projectEvent.getProjectCode());

        //创建项目信息及配置默认方案
        projectInfoService.createProject(projectEvent);
        projectConfigService.create(projectEvent.getProjectId(), stateMachineScheme.getId(), null);
        //初始化项目数据【todo】

        return data;
    }


    @SagaTask(code = "issue-init-organization",
            description = "issue消费创建组织初始化数据",
            sagaCode = "org-create-organization",
            seq = 3)
    public String handleOrgaizationInitByConsumeSagaTask(String data) {
        OrganizationEvent organizationEvent = JSONObject.parseObject(data, OrganizationEvent.class);
        loggerInfo(organizationEvent);
        //组织层初始化五种问题类型及其关联方案
        issueTypeService.initIssueTypeByConsumeCreateOrganization(organizationEvent.getOrganizationId());
        return data;
    }

}
