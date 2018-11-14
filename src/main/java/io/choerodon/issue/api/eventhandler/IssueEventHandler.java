package io.choerodon.issue.api.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.issue.api.dto.payload.OrganizationCreateEventPayload;
import io.choerodon.issue.api.dto.payload.OrganizationRegisterPayload;
import io.choerodon.issue.api.dto.payload.ProjectEvent;
import io.choerodon.issue.api.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static io.choerodon.issue.infra.utils.SagaTopic.Organization.*;
import static io.choerodon.issue.infra.utils.SagaTopic.Project.PROJECT_CREATE;
import static io.choerodon.issue.infra.utils.SagaTopic.Project.TASK_PROJECT_UPDATE;

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
    private IssueTypeService issueTypeService;
    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;
    @Autowired
    private IssueTypeSchemeService issueTypeSchemeService;
    @Autowired
    private PriorityService priorityService;


    /**
     * 创建项目事件
     *
     * @param data data
     */
    @SagaTask(code = TASK_PROJECT_UPDATE,
            description = "创建项目事件",
            sagaCode = PROJECT_CREATE,
            seq = 3)
    public String handleProjectInitByConsumeSagaTask(String data) {
        ProjectEvent projectEvent = JSONObject.parseObject(data, ProjectEvent.class);
        LOGGER.info("接受创建项目消息{}", data);
        //创建项目时创建默认状态机方案
        stateMachineSchemeService.initByConsumeCreateProject(projectEvent);
        //创建项目时创建默认问题类型方案
        issueTypeSchemeService.initByConsumeCreateProject(projectEvent.getProjectId(), projectEvent.getProjectCode());
        //创建项目信息及配置默认方案
        projectInfoService.createProject(projectEvent.getProjectId(), projectEvent.getProjectCode());
        return data;
    }


    @SagaTask(code = TASK_ORG_CREATE,
            description = "创建组织事件",
            sagaCode = ORG_CREATE,
            seq = 1)
    public String handleOrgaizationCreateByConsumeSagaTask(String data) {
        LOGGER.info("消费创建组织消息{}", data);
        OrganizationCreateEventPayload organizationEventPayload = JSONObject.parseObject(data, OrganizationCreateEventPayload.class);
        Long orgId = organizationEventPayload.getId();
        //注册组织初始化六种问题类型
        issueTypeService.initIssueTypeByConsumeCreateOrganization(orgId);
        //注册组织初始化优先级
        priorityService.initProrityByOrganization(Arrays.asList(orgId));
        return data;
    }

    @SagaTask(code = TASK_ORG_REGISTER,
            description = "注册组织事件",
            sagaCode = ORG_REGISTER,
            seq = 1)
    public String handleOrgaizationRegisterByConsumeSagaTask(String data) {
        LOGGER.info("消费创建组织消息{}", data);
        OrganizationRegisterPayload payload = JSONObject.parseObject(data, OrganizationRegisterPayload.class);
        Long organizationId = payload.getOrganizationId();
        //注册组织初始化六种问题类型
        issueTypeService.initIssueTypeByConsumeCreateOrganization(organizationId);
        //注册组织初始化优先级
        priorityService.initProrityByOrganization(Arrays.asList(organizationId));
        return data;
    }
}
