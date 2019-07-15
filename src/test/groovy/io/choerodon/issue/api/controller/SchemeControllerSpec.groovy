package io.choerodon.issue.api.controller

import com.alibaba.fastjson.JSON
import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.vo.PriorityVO
import io.choerodon.issue.api.vo.StatusVO
import io.choerodon.issue.api.vo.payload.OrganizationCreateEventPayload
import io.choerodon.issue.api.vo.payload.ProjectEvent
import io.choerodon.issue.app.eventhandler.IssueEventHandler
import io.choerodon.issue.infra.dto.*
import io.choerodon.issue.infra.enums.ProjectCategory
import io.choerodon.issue.infra.enums.StatusType
import io.choerodon.issue.infra.mapper.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/12/13
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class SchemeControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate
    @Autowired
    ProjectConfigMapper projectConfigMapper
    @Autowired
    IssueTypeSchemeMapper issueTypeSchemeMapper
    @Autowired
    StateMachineSchemeMapper schemeMapper
    @Autowired
    StatusMapper statusMapper
    @Autowired
    IssueTypeMapper issueTypeMapper
    @Autowired
    StateMachineSchemeConfigMapper configMapper
    @Autowired
    StateMachineSchemeConfigDraftMapper configDraftMapper
    @Shared
    Long organizationId = 1L
    @Shared
    Long projectId = 1L
    @Shared
    Boolean isFirst = true
    @Autowired
    IssueEventHandler issueEventHandler
    @Autowired
    PriorityMapper priorityMapper
    @Shared
    List<StatusDTO> statuses
    @Shared
    List<IssueTypeDTO> issueTypes

    void setup() {
        if (isFirst) {
            projectConfigMapper.delete(new ProjectConfigDTO())
            issueTypeSchemeMapper.delete(new IssueTypeSchemeDTO())
            priorityMapper.delete(new PriorityDTO())
            schemeMapper.delete(new StateMachineSchemeDTO())
            configMapper.delete(new StateMachineSchemeConfigDTO())
            configDraftMapper.delete(new StateMachineSchemeConfigDraftDTO())
            statusMapper.delete(new StatusDTO())
            OrganizationCreateEventPayload organizationEvent = new OrganizationCreateEventPayload()
            organizationEvent.id = 1
            issueEventHandler.handleOrgaizationCreateByConsumeSagaTask(JSON.toJSONString(organizationEvent))
            ProjectEvent projectEvent = new ProjectEvent()
            projectEvent.projectId = 1
            projectEvent.projectCode = "test"
            projectEvent.projectCategory = ProjectCategory.AGILE
            issueEventHandler.handleProjectInitByConsumeSagaTask(JSON.toJSONString(projectEvent))
            System.out.print("初始化数据成功")
            isFirst = false
            //获取状态列表
            StatusDTO status = new StatusDTO()
            status.organizationId = organizationId
            statuses = statusMapper.select(status)
            //获取问题类型列表
            IssueTypeDTO issueType = new IssueTypeDTO()
            issueType.organizationId = organizationId
            issueTypes = issueTypeMapper.select(issueType)
        }
    }

    def "queryIssueTypesByProjectId"() {
        when: '查询项目的问题类型列表'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/schemes/query_issue_types?apply_type={apply_type}", List, projectId, "agile")

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body.size() == 6
    }

    def "queryIssueTypesWithStateMachineIdByProjectId"() {
        when: '查询项目的问题类型列表，带对应的状态机id'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/schemes/query_issue_types_with_sm_id?apply_type={apply_type}", List, projectId, "agile")

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body.size() == 6
    }

    def "queryTransformsByProjectId"() {
        when: '查询项目下某个问题类型拥有的转换（包含可以转换到的状态）'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/schemes/query_transforms?current_status_id={current_status_id}&issue_id={issue_id}&issue_type_id={issue_type_id}&apply_type={apply_type}",
                List, projectId, statuses.get(1).id, 1L, issueTypes.get(0).id, "agile")

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body.size() != 0
    }

    def "queryTransformsMapByProjectId"() {
        when: '查询项目下所有问题类型所有状态对应的转换'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/schemes/query_transforms_map?apply_type={apply_type}",
                Object, projectId, "agile")

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.statusCode.value() == 200
    }

    def "queryStatusByIssueTypeId"() {
        when: '查询项目下某个问题类型的所有状态'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/schemes/query_status_by_issue_type_id?issue_type_id={issue_type_id}&apply_type={apply_type}",
                List, projectId, issueTypes.get(0).id, "agile")

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body.size() != 0
    }

    def "queryStatusByProjectId"() {
        when: '查询项目下的所有状态'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/schemes/query_status_by_project_id?apply_type={apply_type}",
                List, projectId, "agile")

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body.size() != 0
    }

    def "queryStateMachineId"() {
        when: '查询项目的问题类型对应的状态机id'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/schemes/query_state_machine_id?apply_type={apply_type}&issue_type_id={issue_type_id}",
                Long, projectId, "agile", issueTypes.get(0).id)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body != null
    }

    def "createStatusForAgile"() {
        given: "准备数据"
        StatusVO statusDTO = new StatusVO()
        statusDTO.organizationId = organizationId
        statusDTO.type = StatusType.TODO
        statusDTO.name = "XX2"
        statusDTO.description = "XX"
        statusDTO.code = "XX"
        statusDTO.canDelete = false
        when: '查询项目的问题类型对应的状态机id'
        def entity = restTemplate.postForEntity("/v1/projects/{project_id}/schemes/create_status_for_agile?applyType=agile",
                statusDTO, StatusVO, projectId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: "期望比较"
        entity.body.code == "XX"

    }

    def "checkCreateStatusForAgile"() {
        when: '查询项目的问题类型对应的状态机id'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/schemes/check_create_status_for_agile?applyType=agile",
                Boolean, projectId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()
        expect: '期望验证'
        entity.body
    }

    def "removeStatusForAgile"() {
        given: '查询项目的问题类型对应的状态机id'
        def entity = restTemplate.delete("/v1/projects/{project_id}/schemes/remove_status_for_agile?status_id={status_id}&applyType=agile",
                projectId, statuses.get(1).id)

        expect: '返回结果'
        entity == null
    }

    def "queryDefaultByOrganizationId"() {
        when: '根据项目id查询组织默认优先级'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/priority/default",
                PriorityVO, projectId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()
        PriorityVO priorityDTO = entity.body

        expect: '期望验证'
        priorityDTO != null
    }


    def "queryWorkFlowFirstStatus"() {
        when: '查询工作流第一个状态'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/status/query_first_status?applyType={applyType}&issueTypeId={issueTypeId}&organizationId={organizationId}",
                Long, projectId, "agile", issueTypes.get(0).id, organizationId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body == 1
    }
}
