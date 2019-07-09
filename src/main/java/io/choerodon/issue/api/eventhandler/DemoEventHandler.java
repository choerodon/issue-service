package io.choerodon.issue.api.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.issue.api.dto.payload.OrganizationRegisterEventPayload;
import io.choerodon.issue.api.dto.payload.ProjectEvent;
import io.choerodon.issue.api.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/27.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class DemoEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoEventHandler.class);
    private static final String REGISTER_ISSUE_INIT_ORG = "register-issue-init-org";
    private static final String REGISTER_ISSUE_INIT_PROJECT = "register-issue-init-project";
    private static final String REGISTER_ORG = "register-org";

    @Autowired
    private InitService initService;
    @Autowired
    private IssueTypeService issueTypeService;
    @Autowired
    private PriorityService priorityService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;
    @Autowired
    private IssueTypeSchemeService issueTypeSchemeService;

    @SagaTask(code = REGISTER_ISSUE_INIT_ORG,
            description = "demo创建组织事件",
            sagaCode = REGISTER_ORG,
            seq = 40)
    public OrganizationRegisterEventPayload orgCreateForDemoInit(String data) {
        LOGGER.info("demo消费创建组织消息{}", data);
        OrganizationRegisterEventPayload organizationRegisterEventPayload = JSONObject.parseObject(data, OrganizationRegisterEventPayload.class);
        Long organizationId = organizationRegisterEventPayload.getOrganization().getId();
        //注册组织初始化问题类型
        issueTypeService.initIssueTypeByConsumeCreateOrganization(organizationId);
        //注册组织初始化优先级
        priorityService.initProrityByOrganization(Arrays.asList(organizationId));
        //初始化状态
        initService.initStatus(organizationId);
        //初始化默认状态机
        initService.initDefaultStateMachine(organizationId);
        return organizationRegisterEventPayload;
    }

    @SagaTask(code = REGISTER_ISSUE_INIT_PROJECT,
            description = "demo创建项目事件",
            sagaCode = REGISTER_ORG,
            seq = 110)
    public OrganizationRegisterEventPayload projectCreateForDemoInit(String data) {
        LOGGER.info("demo接受创建项目消息{}", data);
        OrganizationRegisterEventPayload organizationRegisterEventPayload = JSONObject.parseObject(data, OrganizationRegisterEventPayload.class);
        ProjectEvent projectEvent = new ProjectEvent();
        projectEvent.setProjectId(organizationRegisterEventPayload.getProject().getId());
        projectEvent.setProjectCode(organizationRegisterEventPayload.getProject().getCode());
        projectEvent.setProjectName(organizationRegisterEventPayload.getProject().getName());
        //创建项目时创建默认状态机方案
        stateMachineSchemeService.initByConsumeCreateProject(projectEvent);
        //创建项目时创建默认问题类型方案
        issueTypeSchemeService.initByConsumeCreateProject(projectEvent.getProjectId(), projectEvent.getProjectCode());
        //创建项目信息及配置默认方案
        projectInfoService.createProject(projectEvent.getProjectId(), projectEvent.getProjectCode());
        return organizationRegisterEventPayload;
    }
}
