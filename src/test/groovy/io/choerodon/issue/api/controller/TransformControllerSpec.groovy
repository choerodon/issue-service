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
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author shinan.chen
 * @since 2018/12/19
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Import(IntegrationTestConfiguration)
class TransformControllerSpec extends Specification {
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
    String baseUrl = '/v1/organizations/{organization_id}/state_machine_transform'
    @Shared
    List<StateMachineTransformDraftDTO> transformDrafts = new ArrayList<>()
    /**
     * 每次执行测试之前：初始化
     */
    void setup() {
        //创建2个状态
        StatusDTO status = new StatusDTO()
        status.setId(1L)
        status.setName("新状态1")
        status.setDescription("新状态1")
        status.setOrganizationId(testOrganizationId)
        status.setType(StatusType.DOING)
        statusMapper.insert(status)
        StatusDTO status2 = new StatusDTO()
        status2.setId(2L)
        status2.setName("新状态2")
        status2.setDescription("新状态2")
        status2.setOrganizationId(testOrganizationId)
        status2.setType(StatusType.DOING)
        statusMapper.insert(status2)
        StatusDTO status3 = new StatusDTO()
        status3.setId(3L)
        status3.setName("新状态3")
        status3.setDescription("新状态3")
        status3.setOrganizationId(testOrganizationId)
        status3.setType(StatusType.DOING)
        statusMapper.insert(status3)
        //创建状态机
        StateMachineDTO stateMachine = new StateMachineDTO()
        stateMachine.setId(1L)
        stateMachine.setOrganizationId(testOrganizationId)
        stateMachine.setName("新状态机")
        stateMachine.setDescription("新状态机")
        stateMachine.setStatus(StateMachineStatus.CREATE)
        stateMachine.setDefault(false)
        stateMachineMapper.insert(stateMachine)
        //创建开始节点
        StateMachineNodeDraftDTO nodeStartDraft = new StateMachineNodeDraftDTO()
        nodeStartDraft.setId(1L)
        nodeStartDraft.setOrganizationId(testOrganizationId)
        nodeStartDraft.setStatusId(null)
        nodeStartDraft.setType(InitNode.START.type)
        nodeStartDraft.setPositionX(InitNode.START.positionX)
        nodeStartDraft.setPositionY(InitNode.START.positionY)
        nodeStartDraft.setStateMachineId(1L)
        nodeDraftMapper.insert(nodeStartDraft)
        //创建初始节点
        StateMachineNodeDraftDTO nodeInitDraft = new StateMachineNodeDraftDTO()
        nodeInitDraft.setId(2L)
        nodeInitDraft.setOrganizationId(testOrganizationId)
        nodeInitDraft.setStatusId(1L)
        nodeInitDraft.setType(InitNode.INIT.type)
        nodeInitDraft.setPositionX(InitNode.INIT.positionX)
        nodeInitDraft.setPositionY(InitNode.INIT.positionY)
        nodeInitDraft.setStateMachineId(1L)
        nodeDraftMapper.insert(nodeInitDraft)
        //创建初始转换
        StateMachineTransformDraftDTO transformInitDraft = new StateMachineTransformDraftDTO()
        transformInitDraft.id = 1L
        transformInitDraft.organizationId = testOrganizationId
        transformInitDraft.stateMachineId = 1L
        transformInitDraft.type = TransformType.INIT
        transformInitDraft.name = "初始转换"
        transformInitDraft.conditionStrategy = TransformConditionStrategy.ALL
        transformInitDraft.startNodeId = 1L
        transformInitDraft.endNodeId = 2L
        transformDraftMapper.insert(transformInitDraft)
        transformDrafts.add(transformInitDraft)
        //发布状态机
        stateMachineService.deploy(testOrganizationId, 1L, true)
        //多创建一个节点和一个转换
        StateMachineNodeDraftDTO node1 = new StateMachineNodeDraftDTO()
        node1.setId(3L)
        node1.setOrganizationId(testOrganizationId)
        node1.setStatusId(3L)
        node1.setType(NodeType.CUSTOM)
        node1.setPositionX(100L)
        node1.setPositionY(100L)
        node1.setStateMachineId(1L)
        node1.allStatusTransformId = 2L
        nodeDraftMapper.insert(node1)
        StateMachineTransformDraftDTO newTransform = new StateMachineTransformDraftDTO()
        newTransform.id = 2L
        newTransform.organizationId = testOrganizationId
        newTransform.stateMachineId = 1L
        newTransform.type = TransformType.ALL
        newTransform.name = "全部转换"
        newTransform.conditionStrategy = TransformConditionStrategy.ALL
        newTransform.startNodeId = null
        newTransform.endNodeId = 3L
        transformDraftMapper.insert(newTransform)
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
        StateMachineTransformDTO transformDTO = new StateMachineTransformDTO()
        transformDTO.name = name
        transformDTO.stateMachineId = stateMachineId
        transformDTO.organizationId = testOrganizationId
        transformDTO.endNodeId = endNodeId
        transformDTO.startNodeId = startNodeId
        transformDTO.conditionStrategy = TransformConditionStrategy.ALL
        when: '创建转换（草稿）'
        HttpEntity<StateMachineTransformDTO> httpEntity = new HttpEntity<>(transformDTO)
        def entity = restTemplate.exchange(baseUrl + "?stateMachineId=" + stateMachineId, HttpMethod.POST, httpEntity, StateMachineTransformDTO, testOrganizationId)

        then: '状态码为200，创建成功'
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
        name  | startNodeId | endNodeId | stateMachineId || expRequest | expResponse
        "新转换" | 2L          | 3L        | 1L             || true       | true
        "新转换" | null        | 3L        | 1L             || true       | false
        "新转换" | 2L          | 3L        | 2L             || true       | false
        null  | 2L          | 3L        | 1L             || true       | false
        "新转换" | 2L          | 3L        | null           || false      | false
    }

    def "update"() {
        given: '准备工作'
        def testStateMachineId = stateMachineId
        def testTransformId = transformId
        def transformDraftDTO = new StateMachineTransformDTO()
        BeanUtils.copyProperties(transformDrafts.get(0), transformDraftDTO)
        transformDraftDTO.setId(testTransformId)
        transformDraftDTO.setObjectVersionNumber(1L)
        transformDraftDTO.setName(name)
        when: '更新转换（草稿）'
        HttpEntity<StateMachineTransformDTO> httpEntity = new HttpEntity<>(transformDraftDTO)
        def entity = restTemplate.exchange(baseUrl + '/{transform_id}?stateMachineId=' + testStateMachineId, HttpMethod.PUT, httpEntity, StateMachineTransformDTO, testOrganizationId, testTransformId)

        then: '状态码为200，更新成功'
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
        stateMachineId | transformId | name   || expRequest | expResponse
        1L             | 1L          | "test" || true       | true
        null           | 1L          | "test" || false      | false
    }

    def "deleteTransform"() {
        given: '准备工作'
        def testStateMachineId = stateMachineId
        def testTransformId = transformId
        when: '删除转换（草稿）'
        def entity = restTemplate.exchange(baseUrl + '/{transform_id}?stateMachineId=' + testStateMachineId, HttpMethod.DELETE, null, Object, testOrganizationId, testTransformId)

        then: '状态码为200，更新成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null && entity.getStatusCode() == HttpStatus.NO_CONTENT) {
                    actResponse = true
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse
        where: '测试用例：'
        stateMachineId | transformId || expRequest | expResponse
        1L             | 1L          || true       | false
        1L             | null        || false      | false
        null           | 1L          || false      | false
        2L             | 1L          || true       | false
        1L             | 2L          || true       | false
    }

    def "queryById"() {
        given: '准备工作'
        def queryId = transformId

        when: '根据id获取转换（草稿）'
        def entity = restTemplate.exchange(baseUrl + "/{transform_id}", HttpMethod.GET, null, StateMachineNodeVO, testOrganizationId, queryId)

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
        transformId || expRequest | expResponse
        1L          || true       | true
        9999L       || true       | false
        null        || false      | false
    }

    def "createAllStatusTransform"() {
        given: '准备工作'
        def url = baseUrl + "/create_type_all?1=1"
        if (stateMachineId != null) {
            url = url + "&state_machine_id=" + stateMachineId
        }
        if (endNodeId != null) {
            url = url + "&end_node_id=" + endNodeId
        }
        when: '校验转换名字是否未被使用'
        def entity = restTemplate.exchange(url, HttpMethod.POST, null, StateMachineTransformDTO, testOrganizationId)

        then: '状态码为200，更新成功'
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
        stateMachineId | endNodeId || expRequest | expResponse
        1L             | 2L        || true       | true
        1L             | 3L        || true       | false
        1L             | 999L      || true       | false
        999L           | 2L        || true       | false
        null           | null      || false      | false
    }

    def "deleteAllStatusTransform"() {
        given: '准备工作'
        def testTransformId = transformId
        when: '删除【全部】转换（草稿）'
        def entity = restTemplate.exchange(baseUrl + '/delete_type_all/{transform_id}', HttpMethod.DELETE, null, Object, testOrganizationId, testTransformId)

        then: '状态码为200，更新成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null && entity.getBody() instanceof Boolean) {
                    actResponse = entity.getBody()
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse
        where: '测试用例：'
        transformId || expRequest | expResponse
        2L          || true       | true
        1L          || true       | false
        999L        || true       | false
        null        || false      | false
    }

    def "updateConditionStrategy"() {
        given: '准备工作'
        def testTransformId = transformId
        def url = baseUrl + "/update_condition_strategy/{transform_id}?1=1"
        if (conditionStrategy != null) {
            url = url + "&condition_strategy=" + conditionStrategy
        }
        when: '更改条件策略'
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, Object, testOrganizationId, testTransformId)

        then: '状态码为200，更新成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null && entity.getBody() instanceof Boolean) {
                    actResponse = entity.getBody()
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse
        where: '测试用例：'
        transformId | conditionStrategy              || expRequest | expResponse
        1L          | TransformConditionStrategy.ALL || true       | true
        1L          | "xx"                           || true       | false
        null        | TransformConditionStrategy.ALL || false      | false
    }
}
