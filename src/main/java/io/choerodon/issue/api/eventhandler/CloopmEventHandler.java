package io.choerodon.issue.api.eventhandler;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.issue.api.dto.FieldDTO;
import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.dto.PriorityDTO;
import io.choerodon.issue.api.dto.payload.OrganizationEvent;
import io.choerodon.issue.api.dto.payload.ProjectEvent;
import io.choerodon.issue.api.service.FieldService;
import io.choerodon.issue.api.service.IssueTypeService;
import io.choerodon.issue.api.service.PriorityService;
import io.choerodon.issue.api.service.ProjectInfoService;
import io.choerodon.issue.infra.enums.FieldNameE;
import io.choerodon.issue.infra.enums.IssueTypeE;
import io.choerodon.issue.infra.enums.PriorityE;
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

        for (PriorityE priority : PriorityE.values()) {
            PriorityDTO priorityDTO = new PriorityDTO();
            priorityDTO.setName(priority.value());
            priorityService.create(organizationEvent.getOrganizationId(), priorityDTO);
        }

        for (IssueTypeE issueType : IssueTypeE.values()) {
            IssueTypeDTO issueTypeDTO = new IssueTypeDTO();
            issueTypeDTO.setName(issueType.value());
            issueTypeService.create(organizationEvent.getOrganizationId(), issueTypeDTO);
        }

        for (FieldNameE fieldNameE : FieldNameE.values()) {
            FieldDTO fieldDTO = new FieldDTO();
            fieldDTO.setName(fieldNameE.value());
            fieldDTO.setType(fieldNameE.type());
            fieldService.create(organizationEvent.getOrganizationId(), fieldDTO);
        }
        return data;
    }


}
