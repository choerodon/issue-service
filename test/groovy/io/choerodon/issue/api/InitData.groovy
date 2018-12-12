package io.choerodon.issue.api

import com.alibaba.fastjson.JSON
import io.choerodon.issue.api.dto.payload.OrganizationCreateEventPayload
import io.choerodon.issue.api.dto.payload.ProjectEvent
import io.choerodon.issue.api.eventhandler.IssueEventHandler
import io.choerodon.issue.api.service.StateMachineSchemeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
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
    StateMachineSchemeService stateMachineSchemeService

    @Override
    void run(String... args) throws Exception {
        OrganizationCreateEventPayload payload = new OrganizationCreateEventPayload()
        payload.id = 1
        issueEventHandler.handleOrgaizationCreateByConsumeSagaTask(JSON.toJSONString(payload))
        ProjectEvent projectEvent = new ProjectEvent()
        projectEvent.projectId = 1
        projectEvent.projectCode = "test"
        issueEventHandler.handleProjectInitByConsumeSagaTask(JSON.toJSONString(projectEvent))
    }
}
