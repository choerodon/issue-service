package io.choerodon.issue.app.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.issue.api.vo.payload.OrganizationCreateEventPayload;
import io.choerodon.issue.api.vo.payload.ProjectEvent;
import io.choerodon.issue.app.service.*;
import io.choerodon.issue.infra.enums.ProjectCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static io.choerodon.issue.infra.utils.SagaTopic.Organization.ORG_CREATE;
import static io.choerodon.issue.infra.utils.SagaTopic.Organization.TASK_ORG_CREATE;
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
    @Autowired
    private InitService initService;

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
        if (ProjectCategory.AGILE.equals(projectEvent.getProjectCategory())) {
            //创建项目时创建默认状态机方案
            stateMachineSchemeService.initByConsumeCreateProject(projectEvent);
            //创建项目时创建默认问题类型方案
            issueTypeSchemeService.initByConsumeCreateProject(projectEvent.getProjectId(), projectEvent.getProjectCode());
        } else if (ProjectCategory.PROGRAM.equals(projectEvent.getProjectCategory())) {
            //创建项目群时创建默认状态机方案
            stateMachineSchemeService.initByConsumeCreateProgram(projectEvent);
            //创建项目群时创建默认问题类型方案
            issueTypeSchemeService.initByConsumeCreateProgram(projectEvent.getProjectId(), projectEvent.getProjectCode());
        }
        //创建项目信息
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
        Long organizationId = organizationEventPayload.getId();
        //注册组织初始化问题类型
        issueTypeService.initIssueTypeByConsumeCreateOrganization(organizationId);
        //注册组织初始化优先级
        priorityService.initProrityByOrganization(Arrays.asList(organizationId));
        //初始化状态
        initService.initStatus(organizationId);
        //初始化默认状态机
        initService.initDefaultStateMachine(organizationId);
        return data;
    }
}
