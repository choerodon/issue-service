package io.choerodon.issue.api.controller

import com.github.pagehelper.PageInfo
import io.choerodon.asgard.saga.dto.StartInstanceDTO
import io.choerodon.asgard.saga.feign.SagaClient
import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.vo.StateMachineListVO
import io.choerodon.issue.api.vo.StateMachineVO
import io.choerodon.issue.app.service.InitService
import io.choerodon.issue.app.service.StateMachineService
import io.choerodon.issue.infra.dto.*
import io.choerodon.issue.infra.enums.StateMachineStatus
import io.choerodon.issue.infra.mapper.*
import org.mockito.Matchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author shinan.chen
 * @since 2018/12/10
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class StateMachineControllerSpec extends Specification {
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
    @Shared
    SagaClient sagaClient
    @Shared
    def needInit = true
    @Shared
    def needClean = false
    @Shared
    Long testOrganizationId = 2L
    @Shared
    Long testProjectId = 2L
    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/state_machine'
    @Shared
    def stateMachineList = []
    @Shared
    def stateMachineIds = []
    /**
     * 初始化
     */
    void setup() {
        if (needInit) {
            needInit = false
            //初始化状态
            initService.initStatus(testOrganizationId)
            //初始化默认状态机
            def stateMachineId = initService.initDefaultStateMachine(testOrganizationId)
            //发布状态机
            stateMachineService.deploy(testOrganizationId, stateMachineId, true)
            //新增一个状态机
            StateMachineVO stateMachine1 = new StateMachineVO()
            stateMachine1.setStatus(StateMachineStatus.CREATE)
            stateMachine1.setName("新状态机")
            stateMachine1.setDescription("描述")
            stateMachine1.setOrganizationId(testOrganizationId)
            stateMachine1 = stateMachineService.create(testOrganizationId, stateMachine1)
            stateMachineList.add(stateMachine1)
            stateMachineIds.add(stateMachine1.getId())
            //新增一个状态机
            StateMachineVO stateMachine2 = new StateMachineVO()
            stateMachine2.setStatus(StateMachineStatus.CREATE)
            stateMachine2.setName("新新状态机")
            stateMachine2.setDescription("描述")
            stateMachine2.setOrganizationId(testOrganizationId)
            stateMachine2 = stateMachineService.create(testOrganizationId, stateMachine2)
            stateMachineList.add(stateMachine2)
            stateMachineIds.add(stateMachine2.getId())

            sagaClient = Mockito.mock(SagaClient.class);
            Mockito.when(sagaClient.startSaga(Matchers.anyString(), Matchers.any(StartInstanceDTO.class))).thenReturn(null);
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

    def "pagingQuery"() {
        given: '准备工作'
        def url = baseUrl + "?1=1"
        if (name != null) {
            url = url + "&name=" + name
        }
        if (description != null) {
            url = url + "&description=" + description
        }
        if (param != null) {
            url = url + "&param=" + param
        }
        when: '分页查询'
        ParameterizedTypeReference<PageInfo<StateMachineListVO>> typeRef = new ParameterizedTypeReference<PageInfo<StateMachineListVO>>() {
        }
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, testOrganizationId)

        then: '返回结果'
        def actRequest = false
        def actResponseSize = 0
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    actResponseSize = entity.getBody().size
                }
            }
        }
        actRequest == expRequest
        actResponseSize == expResponseSize

        where: '测试用例：'
        name       | description | param || expRequest | expResponseSize
        null       | null        | null  || true       | 3
        '默认'       | null        | null  || true       | 1
        null       | null        | '默认'  || true       | 1
        'notFound' | null        | null  || true       | 0
    }

    def "create"() {
        given: '准备工作'
        StateMachineVO stateMachine = new StateMachineVO()
        stateMachine.setName(testName)
        stateMachine.setDescription(testDescription)
        stateMachine.setOrganizationId(testOrganizationId)
        when: '创建状态机'
        HttpEntity<StateMachineVO> httpEntity = new HttpEntity<>(stateMachine)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, StateMachineVO, testOrganizationId)

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
        testName | testDescription     || expRequest | expResponse
        '新状态机1'  | 'test-description1' || true       | true
        '默认状态机'  | 'test-description1' || true       | false
        null     | 'test-description1' || true       | false
    }

    def "update"() {
        given: '准备工作'
        StateMachineVO stateMachine = stateMachineList.get(0)
        stateMachine.setName(updateName)

        when: '更新状态'
        HttpEntity<StateMachineVO> httpEntity = new HttpEntity<>(stateMachine)
        def entity = restTemplate.exchange(baseUrl + '/{state_machine_id}', HttpMethod.PUT, httpEntity, StateMachineVO, testOrganizationId, stateMachine.getId())

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
        updateName || expRequest | expResponse
        '新状态机2099' || true       | true
        '默认状态机'    || true       | false
    }

    def "deploy"() {
        given: '准备工作'
        def queryId = stateMachineId

        when: '发布状态机'
        def entity = restTemplate.exchange(baseUrl + "/deploy/{state_machine_id}", HttpMethod.GET, null, Object, testOrganizationId, queryId)

        then: '状态码为200，调用成功'

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
        stateMachineId     || expRequest | expResponse
        stateMachineIds[0] || true       | true
        stateMachineIds[0] || true       | false
        9999L              || true       | false
        null               || false      | false
    }

    def "queryStateMachineWithConfigDraftById"() {
        given: '准备工作'
        def queryId = stateMachineId

        when: '获取状态机及配置（草稿/新建）'
        def entity = restTemplate.exchange(baseUrl + "/with_config_draft/{state_machine_id}", HttpMethod.GET, null, StateMachineVO, testOrganizationId, queryId)

        then: '状态码为200，调用成功'

        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().getId() != null) {
                        if (entity.getBody().getNodeVOS().size() > 0) {
                            actResponse = true
                        }
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        stateMachineId     || expRequest | expResponse
        stateMachineIds[0] || true       | true
        9999L              || true       | false
    }

    def "queryStateMachineWithConfigOriginById"() {
        given: '准备工作'
        def queryId = stateMachineId

        when: '获取状态机原件及配置（活跃）'
        def entity = restTemplate.exchange(baseUrl + "/with_config_deploy/{state_machine_id}", HttpMethod.GET, null, StateMachineVO, testOrganizationId, queryId)

        then: '状态码为200，调用成功'

        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().getId() != null) {
                        if (entity.getBody().getNodeVOS().size() > 0) {
                            actResponse = true
                        }
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        stateMachineId     || expRequest | expResponse
        stateMachineIds[0] || true       | true
        9999L              || true       | false
    }

    def "queryStateMachineById"() {
        given: '准备工作'
        def queryId = stateMachineId

        when: '获取状态机（无配置）'
        def entity = restTemplate.exchange(baseUrl + "/{state_machine_id}", HttpMethod.GET, null, StateMachineVO, testOrganizationId, queryId)

        then: '状态码为200，调用成功'

        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().getId() != null) {
                        actResponse = true
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        stateMachineId     || expRequest | expResponse
        stateMachineIds[0] || true       | true
        9999L              || true       | false
    }

    def "deleteDraft"() {
        given: '准备工作'
        def deleteStateMachineId = stateMachineId
        println deleteStateMachineId
        when: '删除草稿'
        def entity = restTemplate.exchange(baseUrl + '/delete_draft/{state_machine_id}', HttpMethod.DELETE, null, Object, testOrganizationId, deleteStateMachineId)

        then: '状态码为200，更新成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getStatusCode() == HttpStatus.NO_CONTENT) {
                    actResponse = true
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse
        where: '测试用例：'
        stateMachineId     || expRequest | expResponse
        stateMachineIds[0] || true       | true
        999L               || true       | false
    }

    def "checkName"() {
        given: '准备工作'
        def url = baseUrl + "/check_name?1=1"
        if (name != null) {
            url = url + "&name=" + name
        }

        when: '校验状态机名字是否未被使用'
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, Object, testOrganizationId)

        then: '状态码为200，调用成功'

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
        name    || expRequest | expResponse
        '新新名字'  || true       | false
        '默认状态机' || true       | true
    }

    def "queryAll"() {
        given: '准备工作'
        def queryId = organizationId

        when: '查询组织下的所有状态机'
        ParameterizedTypeReference<List<StateMachineVO>> typeRef = new ParameterizedTypeReference<List<StateMachineVO>>() {
        }
        def entity = restTemplate.exchange(baseUrl + "/query_all", HttpMethod.GET, null, typeRef, queryId)

        then: '状态码为200，调用成功'

        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().size() > 0) {
                        actResponse = true
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        organizationId || expRequest | expResponse
        1L             || true       | true
        9999L          || true       | false
    }

    def "delete"() {
        given: '准备工作'
        def deleteStateMachineId = stateMachineId
        when: '删除状态'
        def entity = restTemplate.exchange(baseUrl + '/{state_machine_id}', HttpMethod.DELETE, null, Object, testOrganizationId, deleteStateMachineId)

        then: '状态码为200，更新成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                    actResponse = true
                    needClean = true
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse
        where: '测试用例：'
        stateMachineId     || expRequest | expResponse
        999L               || true       | false
        stateMachineIds[0] || true       | true
    }

}
