package io.choerodon.issue.api

import com.alibaba.fastjson.JSON
import io.choerodon.issue.api.dto.payload.OrganizationCreateEventPayload
import io.choerodon.issue.api.dto.payload.ProjectEvent
import io.choerodon.issue.api.eventhandler.IssueEventHandler
import io.choerodon.issue.api.service.StateMachineSchemeService
import io.choerodon.issue.infra.feign.StateMachineFeignClient
import io.choerodon.issue.infra.utils.ProjectUtil
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/10/24
 */
@Component
class InitData implements CommandLineRunner {

    @Autowired
    IssueEventHandler issueEventHandler

    @Autowired
    @Qualifier("projectUtil")
    ProjectUtil projectUtil


    @Autowired
    StateMachineSchemeService stateMachineSchemeService

    @Override
    void run(String... args) throws Exception {
        Mockito.when(projectUtil.getOrganizationId(1L)).thenReturn(1L)
        StateMachineFeignClient stateMachineServiceFeign = Mockito.mock(StateMachineFeignClient.class)
        stateMachineSchemeService.setFeign(stateMachineServiceFeign)
        Mockito.when(stateMachineServiceFeign.createStateMachineWithCreateProject(1L, "test")).thenReturn(new ResponseEntity<>(1L, HttpStatus.OK))
        OrganizationCreateEventPayload payload = new OrganizationCreateEventPayload()
        payload.id = 1
        issueEventHandler.handleOrgaizationCreateByConsumeSagaTask(JSON.toJSONString(payload))
        ProjectEvent projectEvent = new ProjectEvent()
        projectEvent.projectId = 1
        projectEvent.projectCode = "test"
        issueEventHandler.handleProjectInitByConsumeSagaTask(JSON.toJSONString(projectEvent))
    }
}
