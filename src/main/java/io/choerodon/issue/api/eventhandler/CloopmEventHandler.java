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
public class CloopmEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloopmEventHandler.class);

    @Autowired
    private ProjectInfoService projectInfoService;
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
    @SagaTask(code = "cloopm-init-project",
            description = "cloopm消费创建项目事件初始化项目数据",
            sagaCode = "iam-create-project",
            seq = 3)
    public String handleProjectInitByConsumeSagaTask(String data) {
        ProjectEvent projectEvent = JSONObject.parseObject(data, ProjectEvent.class);
        loggerInfo(projectEvent);

        //创建项目时创建初始化状态机方案
        stateMachineSchemeService.createSchemeWithCreateProject(projectEvent.getProjectId(),projectEvent.getProjectCode());

        projectInfoService.createProject(projectEvent);
        //初始化项目数据【todo】

        return data;
    }


    @SagaTask(code = "cloopm-init-organization",
            description = "cloopm消费创建组织初始化数据",
            sagaCode = "org-create-organization",
            seq = 3)
    public String handleOrgaizationInitByConsumeSagaTask(String data) {
        OrganizationEvent organizationEvent = JSONObject.parseObject(data, OrganizationEvent.class);
        loggerInfo(organizationEvent);

        //此处有空指针异常，记得处理
//        for (PriorityE priority : PriorityE.values()) {
//            PriorityDTO priorityDTO = new PriorityDTO();
//            priorityDTO.setName(priority.value());
//            priorityService.create(organizationEvent.getOrganizationId(), priorityDTO);
//        }
//
//        for (IssueTypeE issueType : IssueTypeE.values()) {
//            IssueTypeDTO issueTypeDTO = new IssueTypeDTO();
//            issueTypeDTO.setName(issueType.value());
//            issueTypeService.create(organizationEvent.getOrganizationId(), issueTypeDTO);
//        }
//
//        for (FieldNameE fieldNameE : FieldNameE.values()) {
//            FieldDTO fieldDTO = new FieldDTO();
//            fieldDTO.setName(fieldNameE.value());
//            fieldDTO.setType(fieldNameE.type());
//            fieldService.create(organizationEvent.getOrganizationId(), fieldDTO);
//        }
        return data;
    }

}
