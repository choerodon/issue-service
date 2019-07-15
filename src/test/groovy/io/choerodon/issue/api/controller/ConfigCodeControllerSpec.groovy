package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.vo.StateMachineVO
import io.choerodon.issue.app.service.InitService
import io.choerodon.issue.app.service.StateMachineService
import io.choerodon.issue.infra.dto.*
import io.choerodon.issue.infra.enums.*
import io.choerodon.issue.infra.mapper.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author shinan.chen
 * @since 2018/12/13
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class ConfigCodeControllerSpec extends Specification {
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
    ConfigCodeMapper configCodeMapper
    @Autowired
    StateMachineService stateMachineService
    @Autowired
    InitService initService
    @Shared
    def needInit = true
    @Shared
    def needClean = false
    @Shared
    Long testOrganizationId = 2L
    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/config_code'
    @Shared
    def statusList = []
    @Shared
    List<StateMachineDTO> stateMachineList = new ArrayList<>()
    @Shared
    def stateMachineIds = []
    /**
     * 初始化
     */
    void setup() {
        if (needInit) {
            needInit = false
            //初始化状态
            statusList = initService.initStatus(testOrganizationId)
            //初始化默认状态机
            Long stateMachineId = initService.initDefaultStateMachine(testOrganizationId)
            //发布状态机
            stateMachineService.deploy(testOrganizationId, stateMachineId, true)

            StateMachineVO stateMachineVO = stateMachineService.queryStateMachineWithConfigById(testOrganizationId, stateMachineId, false)
            stateMachineList.add(stateMachineVO)
            stateMachineIds.add(stateMachineId)

            //初始化一个状态机
            StateMachineDTO stateMachine = new StateMachineDTO()
            stateMachine.setId(100L)
            stateMachine.setOrganizationId(testOrganizationId)
            stateMachine.setName("新状态机")
            stateMachine.setDescription("新状态机")
            stateMachine.setStatus(StateMachineStatus.CREATE)
            stateMachine.setDefault(false)
            stateMachineMapper.insert(stateMachine)

            //创建初始状态机节点和转换
            initService.createStateMachineDetail(testOrganizationId, 100L, "default")
            //新增一个状态
            StatusDTO status = new StatusDTO()
            status.setId(100L)
            status.setName("新状态")
            status.setDescription("新状态")
            status.setOrganizationId(testOrganizationId)
            status.setType(StatusType.DOING)
            statusMapper.insert(status)
            //新增一个节点
            StateMachineNodeDraftDTO nodeDraft = new StateMachineNodeDraftDTO()
            nodeDraft.id = 100L
            nodeDraft.organizationId = testOrganizationId
            nodeDraft.statusId = 100L
            nodeDraft.type = NodeType.CUSTOM
            nodeDraft.positionX = 100
            nodeDraft.positionY = 100
            nodeDraft.stateMachineId = 10L
            nodeDraft.allStatusTransformId = 100L
            nodeDraftMapper.insert(nodeDraft)
            //新增一个转换
            StateMachineTransformDraftDTO transformDraft = new StateMachineTransformDraftDTO()
            transformDraft.id = 100L
            transformDraft.organizationId = testOrganizationId
            transformDraft.name = "新转换"
            transformDraft.description = "新转换"
            transformDraft.type = TransformType.ALL
            transformDraft.conditionStrategy = TransformConditionStrategy.ALL
            transformDraft.endNodeId = 100L
            transformDraft.startNodeId = 0L
            transformDraft.stateMachineId = 100L
            transformDraftMapper.insert(transformDraft)
            ConfigCodeDTO configCode = new ConfigCodeDTO()
            configCode.service = "agile"
            configCode.type = ConfigType.ACTION
            configCode.name = "发消息"
            configCode.code = "send_action"
            configCodeMapper.insert(configCode)
            //发布状态机
            stateMachineService.deploy(testOrganizationId, 100L, false)
        }
    }
    /**
     * 删除数据
     */
    void cleanup() {
        if (needClean) {
            needClean = false
            //删除状态
            StatusDTO status = new StatusDTO()
            status.organizationId = testOrganizationId
            statusMapper.delete(status)
            //删除状态机
            StateMachineDTO stateMachine = new StateMachineDTO()
            stateMachine.organizationId = testOrganizationId
            stateMachineMapper.delete(stateMachine)
            //删除节点
            StateMachineNodeDTO node = new StateMachineNodeDTO()
            node.organizationId = testOrganizationId
            nodeMapper.delete(node)
            //删除草稿节点
            StateMachineNodeDraftDTO draft = new StateMachineNodeDraftDTO()
            draft.organizationId = testOrganizationId
            nodeDraftMapper.delete(draft)
            //删除转换
            StateMachineTransformDTO transform = new StateMachineTransformDTO()
            transform.organizationId = testOrganizationId
            transformMapper.delete(transform)
            //删除草稿转换
            StateMachineTransformDraftDTO transformDraft = new StateMachineTransformDraftDTO()
            transformDraft.organizationId = testOrganizationId
            transformDraftMapper.delete(transformDraft)
        }
    }

    def "queryByTransformId"() {
        given: '准备工作'
        def testTransformId = transformId
        def testType = type
        when: '获取未配置的条件，验证，后置动作等列表'
        ParameterizedTypeReference<List<ConfigCodeDTO>> typeRef = new ParameterizedTypeReference<List<ConfigCodeDTO>>() {
        }
        def entity = restTemplate.exchange(baseUrl + "/{transform_id}?type=" + testType, HttpMethod.GET, null, typeRef, testOrganizationId, testTransformId)
        then: '状态码为200，创建成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null && entity.getBody().size() > 0) {
                    actResponse = true
                    needClean = true
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse
        where: '测试用例：'
        transformId | type                 || expRequest | expResponse
        100L         | ConfigType.ACTION    || true       | true
        100L         | ConfigType.VALIDATOR || true       | false
    }

    def "clean"() {
        needClean = true
    }
}
