package io.choerodon.issue.api.controller

import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.vo.StateMachineNodeVO
import io.choerodon.issue.app.service.InitService
import io.choerodon.issue.app.service.StateMachineService
import io.choerodon.issue.infra.dto.*
import io.choerodon.issue.infra.enums.*
import io.choerodon.issue.infra.mapper.*
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author shinan.chen
 * @since 2018/12/13
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Import(IntegrationTestConfiguration)
class NodeControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate
    @Autowired
    StatusMapper statusMapper
    @Autowired
    StateMachineMapper stateMachineMapper
    @Autowired
    StateMachineNodeMapper nodeMapper
    @Autowired
    StateMachineNodeDraftMapper nodeDraftMapper
    @Autowired
    StateMachineTransformMapper transformMapper
    @Autowired
    StateMachineTransformDraftMapper transformDraftMapper
    @Autowired
    StateMachineService stateMachineService
    @Autowired
    InitService initService
    @Autowired
    SagaClient sagaClient
    @Shared
    Long testOrganizationId = 2L
    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/state_machine_nodes'
    @Shared
    List<StateMachineNodeDraftDTO> nodeDrafts = new ArrayList<>()
    /**
     * 每次执行测试之前：初始化
     */
    void setup() {
        //创建2个状态
        StatusDTO status = new StatusDTO()
        status.setId(100L)
        status.setName("新状态1")
        status.setDescription("新状态1")
        status.setOrganizationId(testOrganizationId)
        status.setType(StatusType.DOING)
        statusMapper.insert(status)
        StatusDTO status2 = new StatusDTO()
        status2.setId(200L)
        status2.setName("新状态2")
        status2.setDescription("新状态2")
        status2.setOrganizationId(testOrganizationId)
        status2.setType(StatusType.DOING)
        statusMapper.insert(status2)
        //创建状态机
        StateMachineDTO stateMachine = new StateMachineDTO()
        stateMachine.setId(100L)
        stateMachine.setOrganizationId(testOrganizationId)
        stateMachine.setName("新状态机")
        stateMachine.setDescription("新状态机")
        stateMachine.setStatus(StateMachineStatus.CREATE)
        stateMachine.setDefault(false)
        stateMachineMapper.insert(stateMachine)
        //创建开始节点
        StateMachineNodeDraftDTO nodeStartDraft = new StateMachineNodeDraftDTO()
        nodeStartDraft.setId(100L)
        nodeStartDraft.setOrganizationId(testOrganizationId)
        nodeStartDraft.setStatusId(null)
        nodeStartDraft.setType(InitNode.START.type)
        nodeStartDraft.setPositionX(InitNode.START.positionX)
        nodeStartDraft.setPositionY(InitNode.START.positionY)
        nodeStartDraft.setStateMachineId(100L)
        nodeDraftMapper.insert(nodeStartDraft)
        //创建初始节点
        StateMachineNodeDraftDTO nodeInitDraft = new StateMachineNodeDraftDTO()
        nodeInitDraft.setId(200L)
        nodeInitDraft.setOrganizationId(testOrganizationId)
        nodeInitDraft.setStatusId(100L)
        nodeInitDraft.setType(InitNode.INIT.type)
        nodeInitDraft.setPositionX(InitNode.INIT.positionX)
        nodeInitDraft.setPositionY(InitNode.INIT.positionY)
        nodeInitDraft.setStateMachineId(100L)
        nodeDraftMapper.insert(nodeInitDraft)
        nodeDrafts.add(nodeInitDraft)
        //创建初始转换
        StateMachineTransformDraftDTO transformInitDraft = new StateMachineTransformDraftDTO()
        transformInitDraft.id = 100L
        transformInitDraft.organizationId = testOrganizationId
        transformInitDraft.stateMachineId = 100L
        transformInitDraft.type = TransformType.INIT
        transformInitDraft.name = "初始转换"
        transformInitDraft.conditionStrategy = TransformConditionStrategy.ALL
        transformInitDraft.startNodeId = 100L
        transformInitDraft.endNodeId = 200L
        transformDraftMapper.insert(transformInitDraft)
        //发布状态机
        stateMachineService.deploy(testOrganizationId, 100L, true)
        //多创建一个节点
        StatusDTO newStatus = new StatusDTO()
        newStatus.setId(300L)
        newStatus.setName("新状态3")
        newStatus.setDescription("新状态3")
        newStatus.setOrganizationId(testOrganizationId)
        newStatus.setType(StatusType.DOING)
        statusMapper.insert(newStatus)
        StateMachineNodeDraftDTO newNode = new StateMachineNodeDraftDTO()
        newNode.setId(300L)
        newNode.setOrganizationId(testOrganizationId)
        newNode.setStatusId(300L)
        newNode.setType(NodeType.CUSTOM)
        newNode.setPositionX(100L)
        newNode.setPositionY(100L)
        newNode.setStateMachineId(100L)
        nodeDraftMapper.insert(newNode)
    }
    /**
     * 每次执行测试之后：删除数据
     */
    void cleanup() {
        StatusDTO status = new StatusDTO()
        statusMapper.delete(status)
        StateMachineDTO stateMachine = new StateMachineDTO()
        stateMachineMapper.delete(stateMachine)
        StateMachineNodeDraftDTO nodeDraft = new StateMachineNodeDraftDTO()
        nodeDraftMapper.delete(nodeDraft)
        StateMachineTransformDraftDTO transformDraft = new StateMachineTransformDraftDTO()
        transformDraftMapper.delete(transformDraft)
        StateMachineNodeDTO nodeDeploy = new StateMachineNodeDTO()
        nodeMapper.delete(nodeDeploy)
        StateMachineTransformDTO transformDeploy = new StateMachineTransformDTO()
        transformMapper.delete(transformDeploy)
    }

    def "create"() {
        given: '准备工作'
        StateMachineNodeDTO nodeDTO = new StateMachineNodeDTO()
        nodeDTO.statusId = statusId
        nodeDTO.stateMachineId = stateMachineId
        nodeDTO.organizationId = testOrganizationId
        nodeDTO.positionY = 100L
        nodeDTO.positionX = 100L
        when: '创建节点（草稿）'
        HttpEntity<StateMachineNodeDTO> httpEntity = new HttpEntity<>(nodeDTO)
        def entity = restTemplate.exchange(baseUrl + "?stateMachineId=" + stateMachineId, HttpMethod.POST, httpEntity, Object, testOrganizationId)

        then: '状态码为200，创建成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null && entity.getBody() instanceof ArrayList) {
                    actResponse = true
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse
        where: '测试用例：'
        statusId | stateMachineId || expRequest | expResponse
        200L     | 100L           || true       | true
        300L     | 100L           || true       | true
        200L     | 200L           || true       | false
        100L     | 100L           || true       | true
    }

    def "update"() {
        given: '准备工作'
        def testStateMachineId = stateMachineId
        def nodeDraftDTO = new StateMachineNodeDTO()
        BeanUtils.copyProperties(nodeDrafts.get(0), nodeDraftDTO)
        nodeDraftDTO.setObjectVersionNumber(1L)
        nodeDraftDTO.positionX = positionX
        when: '更新节点（草稿）'
        HttpEntity<StateMachineNodeDTO> httpEntity = new HttpEntity<>(nodeDraftDTO)
        def entity = restTemplate.exchange(baseUrl + '/{node_id}?stateMachineId=' + testStateMachineId, HttpMethod.PUT, httpEntity, Object, testOrganizationId, nodeDrafts.get(0).getId())

        then: '状态码为200，更新成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null && entity.getBody() instanceof ArrayList) {
                    actResponse = true
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse
        where: '测试用例：'
        stateMachineId | positionX || expRequest | expResponse
        100L           | 200L      || true       | true
        200L           | 200L      || true       | false
    }

    def "deleteNode"() {
        given: '准备工作'
        def testStateMachineId = stateMachineId
        def testNodeId = nodeId
        when: '删除节点（草稿）'
        def entity = restTemplate.exchange(baseUrl + '/{node_id}?stateMachineId=' + testStateMachineId, HttpMethod.DELETE, null, Object, testOrganizationId, testNodeId)

        then: '状态码为200，更新成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null && entity.getBody() instanceof ArrayList) {
                    actResponse = true
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse
        where: '测试用例：'
        stateMachineId | nodeId || expRequest | expResponse
        100L           | 300L   || true       | true
        200L           | 300L   || true       | false
        100L           | 200L   || true       | false
    }

    def "checkDelete"() {
        given: '准备工作'
        def url = baseUrl + "/check_delete?1=1"
        if (statusId != null) {
            url = url + "&statusId=" + statusId
        }
        if (stateMachineId != null) {
            url = url + "&stateMachineId=" + stateMachineId
        }
        when: '校验是否能删除节点（草稿）'
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, Object, testOrganizationId)

        then: '状态码为200，更新成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null && entity.getBody() instanceof Map) {
                    if (entity.getBody().get("canDelete") != null) {
                        actResponse = entity.getBody().get("canDelete")
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse
        where: '测试用例：'
        stateMachineId | statusId || expRequest | expResponse
        100L           | 300L     || true       | true
        null           | 300L     || false      | false
        100L           | null     || false      | false
        200L           | 300L     || true       | false
        100L           | 999L     || true       | false
    }

    def "queryById"() {
        given: '准备工作'
        def queryId = nodeId

        when: '根据id获取节点（草稿）'
        def entity = restTemplate.exchange(baseUrl + "/{node_id}", HttpMethod.GET, null, StateMachineNodeVO, testOrganizationId, queryId)

        then: '状态码为200，调用成功'

        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null && entity.getBody().getId() != null) {
                    actResponse = true
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        nodeId || expRequest | expResponse
        200L   || true       | true
        9999L  || true       | false
        null   || false      | false
    }
}
