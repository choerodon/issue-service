package io.choerodon.issue.api.controller

import io.choerodon.core.domain.Page
import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.payload.ChangeStatus
import io.choerodon.issue.api.dto.payload.DeployStateMachinePayload
import io.choerodon.issue.infra.feign.dto.StateMachineDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
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
class StateMachineControllerTest extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    Long organizationId = 1L

    def "pagingQuery"() {
        when: '查询项目的问题类型列表'
        def entity = restTemplate.getForEntity("/v1/organizations/{organization_id}/state_machine?page={page}&size={size}", Page, organizationId, 1, 1000)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()
        Page<StateMachineDTO> stateMachineDTOPage = entity.body
        expect: '期望验证'
        stateMachineDTOPage.content.size() == 1
    }

    def "queryProjectIdsMap"() {
        when: '【内部调用】查询状态机关联的项目id列表的Map'
        def entity = restTemplate.getForEntity("/v1/organizations/{organization_id}/state_machine/query_project_ids_map?stateMachineId={stateMachineId}", Map, organizationId, 1)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()
        Map<String, List<Long>> map = entity.body
        expect: '期望验证'
        map.get("test") != null
    }

    def "checkDeleteNode"() {
        when: '内部调用】状态机删除节点的校验，是否可以直接删除'
        def entity = restTemplate.getForEntity("/v1/organizations/{organization_id}/state_machine/check_delete_node?stateMachineId={stateMachineId}&statusId={statusId}", Map, organizationId, 1L, 1L)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()
        Map<String, Object> map = entity.body
        expect: '期望验证'
        map.get("XX") != null
    }

    def "handleStateMachineChangeStatusByStateMachineId"() {
        given: "准备数据"
        ChangeStatus changeStatus = new ChangeStatus()
        List<Long> addIds = new ArrayList<>(1)
        addIds.add(1L)
        List<Long> deleteIds = new ArrayList<>(1)
        addIds.add(2L)
        changeStatus.setAddStatusIds(addIds)
        changeStatus.setDeleteStatusIds(deleteIds)
        when: '【内部调用】发布状态机时对增加与减少的状态进行处理，影响到的项目是否需要增加与减少相应的状态'
        def entity = restTemplate.postForEntity("/v1/organizations/{organization_id}/state_machine/handle_state_machine_change_status_by_state_machine_id?stateMachineId={stateMachineId}", changeStatus, DeployStateMachinePayload, organizationId, 1L)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()
        DeployStateMachinePayload deployStateMachinePayload = entity.body
        expect: '期望验证'
        deployStateMachinePayload.addStatusWithProjects.size() == 1
        deployStateMachinePayload.removeStatusWithProjects.size() == 0
    }

    def "delete"() {
        given: "删除状态机"
        ChangeStatus changeStatus = new ChangeStatus()
        List<Long> addIds = new ArrayList<>(1)
        addIds.add(1L)
        List<Long> deleteIds = new ArrayList<>(1)
        addIds.add(2L)
        changeStatus.setAddStatusIds(addIds)
        changeStatus.setDeleteStatusIds(deleteIds)
        when: '【内部调用】发布状态机时对增加与减少的状态进行处理，影响到的项目是否需要增加与减少相应的状态'
        def entity = restTemplate.exchange("/v1/organizations/{organization_id}/state_machine/{state_machine_id}", HttpMethod.DELETE, null, Object, organizationId, 1L)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful()

        expect: '期望验证'
        entity.body.code == "error.stateMachine.delete"
    }


}
