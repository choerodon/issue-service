package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.PriorityDTO
import io.choerodon.issue.infra.feign.dto.StatusDTO
import io.choerodon.issue.infra.mapper.ProjectConfigMapper
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
class SchemeControllerTest extends Specification {

    @Autowired
    TestRestTemplate restTemplate
    @Autowired
    ProjectConfigMapper projectConfigMapper

    @Shared
    Long organizationId = 1L

    @Shared
    Long projectId = 1L

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
                List, projectId, 1L, 1L, 1L, "agile")

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body.size() == 1
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
                List, projectId, 1L, "agile")

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body.size() == 1
    }

    def "queryStatusByProjectId"() {
        when: '查询项目下的所有状态'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/schemes/query_status_by_project_id?apply_type={apply_type}",
                List, projectId, "agile")

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body.size() == 1
    }

    def "queryStateMachineId"() {
        when: '查询项目的问题类型对应的状态机id'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/schemes/query_state_machine_id?apply_type={apply_type}&issue_type_id={issue_type_id}",
                Long, projectId, "agile", 1L)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body == 1
    }

    def "createStatusForAgile"() {
        given: "准备数据"
        StatusDTO statusDTO = new StatusDTO()
        statusDTO.organizationId = organizationId
        statusDTO.type = "XX2"
        statusDTO.name = "XX2"
        statusDTO.description = "XX"
        statusDTO.code = "XX"
        statusDTO.canDelete = false
        when: '查询项目的问题类型对应的状态机id'
        def entity = restTemplate.postForEntity("/v1/projects/{project_id}/schemes/create_status_for_agile",
                statusDTO, StatusDTO, projectId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: "期望比较"
        entity.body.code == "error.stateMachineScheme.stateMachineInMoreThanOneScheme"

    }

    def "checkCreateStatusForAgile"() {
        when: '查询项目的问题类型对应的状态机id'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/schemes/check_create_status_for_agile",
                Boolean, projectId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()
        expect: '期望验证'
        !entity.body
    }

    def "removeStatusForAgile"() {
        given: '查询项目的问题类型对应的状态机id'
        def entity = restTemplate.delete("/v1/projects/{project_id}/schemes/remove_status_for_agile?status_id={status_id}",
                projectId, 1L)

        expect: '返回结果'
        entity == null


    }

    def "queryDefaultByOrganizationId"() {
        when: '根据项目id查询组织默认优先级'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/priority/default",
                PriorityDTO, projectId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()
        PriorityDTO priorityDTO = entity.body

        expect: '期望验证'
        priorityDTO != null
    }

    def "queryByOrganizationIdList"() {
        when: '根据项目id查询组织优先级列表'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/priority/list_by_org",
                List, projectId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()
        List<PriorityDTO> priorityDTOList = entity.body

        expect: '期望验证'
        priorityDTOList.size() == 6
    }

    def "queryWorkFlowFirstStatus"() {
        when: '查询工作流第一个状态'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/status/query_first_status?applyType={applyType}&issueTypeId={issueTypeId}&organizationId={organizationId}",
                Long, projectId, "agile", 1L, organizationId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body == 1
    }
}
