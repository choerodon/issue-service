package io.choerodon.issue.api.controller

import com.github.pagehelper.PageInfo
import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.StateMachineSchemeConfigDTO
import io.choerodon.issue.api.dto.StateMachineSchemeDTO
import io.choerodon.issue.api.dto.payload.StateMachineSchemeChangeItem
import io.choerodon.issue.api.service.StateMachineSchemeService
import io.choerodon.issue.domain.StateMachineSchemeConfig
import io.choerodon.issue.infra.feign.StateMachineFeignClient
import io.choerodon.issue.infra.mapper.StateMachineSchemeConfigMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class StateMachineSchemeControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    StateMachineSchemeService schemeService
    @Autowired
    StateMachineSchemeConfigMapper stateMachineSchemeConfigMapper

    @Autowired
    private StateMachineFeignClient stateMachineServiceFeign

    @Shared
    Long organizationId = 1L

    @Shared
    Long stateMachineId = 1L

    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/state_machine_scheme'

    @Shared
    List<StateMachineSchemeDTO> list = new ArrayList<>()
    @Shared
    List<StateMachineSchemeChangeItem> stateMachineSchemeChangeItemList = new ArrayList<>()
    @Shared
    List<Long> configIds = new ArrayList<>()

    def "pagingQuery"() {
        when: '查询状态机方案列表'
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
        ParameterizedTypeReference<PageInfo<StateMachineSchemeDTO>> typeRef = new ParameterizedTypeReference<PageInfo<StateMachineSchemeDTO>>() {
        }
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, organizationId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        entity.getBody().getList().size() == size

        where: '测试用例：'
        name              | description     | param  || isSuccess | size
        null              | null            | null   || true      | 2
        'test默认状态机方案【测试】' | null            | null   || true      | 1
        null              | null            | 'test' || true      | 2
        null              | null            | 'xx'   || true      | 0
    }

    def "create"() {
        given: '创建状态机方案'
        StateMachineSchemeDTO schemeDTO = new StateMachineSchemeDTO()
        schemeDTO.setName(name)
        schemeDTO.setDescription(description)

        when: '状态机方案写入数据库'
        HttpEntity<StateMachineSchemeDTO> httpEntity = new HttpEntity<>(schemeDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, StateMachineSchemeDTO, organizationId)

        then: '状态码为200，创建成功'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult
        if (reponseResult) {
            list.add(entity.body)
        }

        where: '测试用例：'
        name         | description         | type     || isSuccess | reponseResult
        'test-name1' | 'test-description1' | 'agile'  || true      | true
        null         | 'test-description1' | 'agile'  || true      | false
        'test-name1' | 'test-description1' | 'agile1' || true      | false
        null         | null                | 'agile'  || true      | false
    }

    def "update"() {
        given: '更新状态机方案'
        StateMachineSchemeDTO schemeDTO = list.get(0)
        schemeDTO.setName(name)
        schemeDTO.setDescription(description)
        schemeDTO.setOrganizationId(organizationId)
        when: '状态机方案写入数据库'
        HttpEntity<StateMachineSchemeDTO> httpEntity = new HttpEntity<>(schemeDTO)
        def entity = restTemplate.exchange(baseUrl + "/{scheme_id}", HttpMethod.PUT, httpEntity, StateMachineSchemeDTO, organizationId, schemeDTO.id)

        then: '更新成功判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        name         | description         || isSuccess | reponseResult
        'test-name1' | 'test-description1' || true      | true
        ""           | 'test-description1' || true      | false
    }

    def "querySchemeWithConfigById"() {
        when: '根据id查询状态机方案对象'
        def entity = restTemplate.exchange(baseUrl + "/query_scheme_with_config/{scheme_id}?isDraft={isDraft}", HttpMethod.GET, null, StateMachineSchemeDTO, organizationId, schemeId, isDraft)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        schemeId       | isDraft || isSuccess | reponseResult
        null           | false    | false     | false
        list.get(0).id | true     | true      | true
        99             | true     | true      | false
    }

    def "createConfig"() {
        given: '设置feign mock'
        StateMachineSchemeConfigDTO configDTO = new StateMachineSchemeConfigDTO()
        configDTO.setIssueTypeId(1L)
        List<StateMachineSchemeConfigDTO> configDTOS = new ArrayList<>()
        configDTOS.add(configDTO)

        when: '创建方案配置'
        HttpEntity<List<StateMachineSchemeConfigDTO>> httpEntity = new HttpEntity<>(configDTOS)
        def entity = restTemplate.exchange(baseUrl + "/create_config/{scheme_id}/{state_machine_id}", HttpMethod.POST, httpEntity, StateMachineSchemeDTO, organizationId, schemeId, stateMachineId)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult
        if (reponseResult) {
            entity.getBody().getConfigDTOs().each { a ->
                configIds.add(a.id)
            }
        }

        where: '测试用例：'
        schemeId            | stateMachineId || isSuccess | reponseResult
//        null                | null           || false     | false
        list.get(0).getId() | 1              || true      | true
    }

    def "checkName"() {
        when: '校验状态机方案名字是否未被使用'
        def url = baseUrl + "/check_name?1=1"
        if (scheme_id != null) {
            url = url + "&scheme_id=" + scheme_id
        }
        if (name != null) {
            url = url + "&name=" + name
        }

        ParameterizedTypeReference<Boolean> typeRef = new ParameterizedTypeReference<Boolean>() {}
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, organizationId)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        entity.getBody() != null && entity.getBody() == reponseResult

        where: '测试用例：'
        scheme_id           | name    || isSuccess | reponseResult
        null                | 'name1' || true      | false
        list.get(0).getId() | 'name1' || true      | false
    }

    def "checkDeploy"() {
        given: "准备数据"
        StateMachineSchemeConfig schemeConfig = new StateMachineSchemeConfig()
        schemeConfig.issueTypeId = 1L
        schemeConfig.schemeId = 1L
        schemeConfig.organizationId = 1L
        schemeConfig.stateMachineId = 1L
        stateMachineSchemeConfigMapper.insert(schemeConfig)
        configIds.add(schemeConfig.id)
        when: '校验发布状态机方案'
        def entity = restTemplate.getForEntity(baseUrl + "/check_deploy/{scheme_id}", List, organizationId, 1L)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful()
    }

    def "deploy"() {
        given: "准备数据"
        HttpEntity<List<StateMachineSchemeChangeItem>> httpEntity = new HttpEntity<>(stateMachineSchemeChangeItemList)
        when: '发布状态机方案'
        def entity = restTemplate.exchange(baseUrl + "/deploy/{scheme_id}?objectVersionNumber=2", HttpMethod.POST, httpEntity, Boolean, organizationId, 1L)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful()

        expect: "校验结果"
        entity.body
    }

    def "updateDeployProgress"() {
        given: "准备数据"
        HttpEntity<List<StateMachineSchemeChangeItem>> httpEntity = new HttpEntity<>(stateMachineSchemeChangeItemList)
        when: '更新状态机方案发布进度'
        def entity = restTemplate.exchange(baseUrl + "/update_deploy_progress/{scheme_id}?deployProgress={deployProgress}", HttpMethod.PUT, httpEntity, Boolean, organizationId, list.get(0).id, 0)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful()

        expect: "校验结果"
        entity.body
    }

    def "deleteDraft"() {
        when: '删除状态机方案草稿'
        def entity = restTemplate.exchange(baseUrl + "/delete_draft/{scheme_id}", HttpMethod.DELETE, null, StateMachineSchemeDTO, organizationId, list.get(0).id)

        then: '结果判断'
        StateMachineSchemeDTO schemeDTO = entity.body
        expect: '测试用例：'
        schemeDTO != null
    }

    def "deleteConfig"() {
        when: '删除方案配置'
        def entity = restTemplate.exchange(baseUrl + "/delete_config/{scheme_id}/{state_machine_id}", HttpMethod.DELETE, null, StateMachineSchemeDTO, organizationId, id, 1L)

        then: '结果判断'
        entity.body

        where: '测试用例：'
        id << list
    }

    def "delete"() {
        when: '删除状态机方案'
        def entity = restTemplate.exchange(baseUrl + "/{scheme_id}", HttpMethod.DELETE, null, Object, organizationId, stateMachineScheme.id)

        then: '删除结果判断'
        entity.body

        where: '测试用例：'
        stateMachineScheme << list
    }


}