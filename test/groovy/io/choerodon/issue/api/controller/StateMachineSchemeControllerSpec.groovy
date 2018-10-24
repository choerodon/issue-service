package io.choerodon.issue.api.controller

import io.choerodon.core.domain.Page
import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.StateMachineSchemeConfigDTO
import io.choerodon.issue.api.dto.StateMachineSchemeDTO
import io.choerodon.issue.api.service.StateMachineSchemeService
import io.choerodon.issue.domain.StateMachineScheme
import io.choerodon.issue.infra.feign.StateMachineFeignClient
import io.choerodon.issue.infra.feign.dto.StateMachineDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    StateMachineSchemeService schemeService;

    @Autowired
    private StateMachineFeignClient stateMachineServiceFeign

    @Shared
    Long orginzationId = 1L;

    @Shared
    Long stateMachineId = 1L;

    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/state_machine_scheme'

    @Shared
    List<StateMachineScheme> list = new ArrayList<>()

    //初始化40条数据 设置mock StateMachineServiceFeign
    def setup() {
        //Mock StateMachineServiceFeign
        // *_表示任何长度的参数
        StateMachineDTO stateMachineDTO = new StateMachineDTO();
        stateMachineDTO.setId(stateMachineId)
        stateMachineDTO.setName("状态机名称")
        stateMachineDTO.setDescription("状态机描述")
        ResponseEntity<StateMachineDTO> responseEntity = new ResponseEntity<>(stateMachineDTO, HttpStatus.OK)
        stateMachineServiceFeign.queryStateMachineById(*_) >> responseEntity

        def name = 'name'
        def description = 'description'
        def testType = '1'
        for (int i = 2; i <= 40; i++) {
            StateMachineScheme scheme = new StateMachineScheme();
            scheme.setId(i);
            scheme.setName(name + i)
            scheme.setDescription(description + i)
            scheme.setOrganizationId(orginzationId)
            int isInsert = schemeService.insert(scheme)
            if (isInsert == 1) {
                list.add(scheme)
            }
        }
    }

    def cleanup() {
        StateMachineScheme del = new StateMachineScheme();
        schemeService.delete(del);//清空数据
        list.clear();
    }

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
        ParameterizedTypeReference<Page<StateMachineSchemeDTO>> typeRef = new ParameterizedTypeReference<Page<StateMachineSchemeDTO>>() {
        };
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, orginzationId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        entity.getBody().size() == size

        where: '测试用例：'
        name     | description     | param  || isSuccess | size
        null     | null            | null   || true      | 20
        'name40' | null            | null   || true      | 1
        null     | 'description40' | null   || true      | 1
        'name40' | 'description40' | null   || true      | 1
        null     | null            | 'name' || true      | 20
    }

    def "create"() {
        given: '创建状态机方案'
        StateMachineSchemeDTO schemeDTO = new StateMachineSchemeDTO();
        schemeDTO.setName(name)
        schemeDTO.setType(type)
        schemeDTO.setDescription(description)

        when: '状态机方案写入数据库'
        HttpEntity<StateMachineSchemeDTO> httpEntity = new HttpEntity<>(schemeDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, StateMachineSchemeDTO, orginzationId)

        then: '状态码为200，创建成功'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        name         | description         | type     || isSuccess | reponseResult
        'test-name1' | 'test-description1' | 'agile'  || true      | true
        null         | 'test-description1' | 'agile'  || true      | false
        'test-name1' | 'test-description1' | 'agile1' || true      | false
        null         | null                | 'agile'  || true      | false
    }

    def "update"() {
        given: '更新状态机方案'
        StateMachineSchemeDTO schemeDTO = new StateMachineSchemeDTO()
        schemeDTO.setName(name)
        schemeDTO.setDescription(description)
        schemeDTO.setObjectVersionNumber(1L)
        schemeDTO.setOrganizationId(orginzationId)
        when: '状态机方案写入数据库'
        HttpEntity<StateMachineSchemeDTO> httpEntity = new HttpEntity<>(schemeDTO)
        def entity = restTemplate.exchange(baseUrl + "/{scheme_id}", HttpMethod.PUT, httpEntity, StateMachineSchemeDTO, orginzationId, schemeId)

        then: '更新成功判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        schemeId | name         | description         || isSuccess | reponseResult
        1        | 'test-name1' | 'test-description1' || true      | true
        1        | null         | 'test-description1' || true      | true
        1        | ""           | 'test-description1' || true      | false
    }

    def "delete"() {
        when: '删除状态机方案'
        def entity = restTemplate.exchange(baseUrl + "/{scheme_id}", HttpMethod.DELETE, null, Object, orginzationId, schemeId)

        then: '删除结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        if (entity.getBody() != null && entity.getBody() instanceof Boolean) {
            entity.getBody() != null && entity.getBody() == reponseResult
        } else if (entity.getBody() != null) {
            Map map = (Map) entity.getBody();
            map.get("failed") == reponseResult;
            map.get("code") == "error.stateMachineScheme.delete";
        }

        where: '测试用例：'
        schemeId || isSuccess | reponseResult
//        null                || true      | false
        1        || true      | true
        99       || true      | true
    }

    def "querySchemeWithConfigById"() {
        when: '根据id查询状态机方案对象'
        def entity = restTemplate.exchange(baseUrl + "/{scheme_id}", HttpMethod.GET, null, StateMachineSchemeDTO, orginzationId, schemeId)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        schemeId || isSuccess | reponseResult
        null     || true      | false
        1        || true      | true
        99       || true      | false
    }

    def "createConfig"() {
        /*given: '设置feign mock'
        StateMachineSchemeConfigDTO configDTO = new StateMachineSchemeConfigDTO();
        configDTO.setIssueTypeId(1L);
        List<StateMachineSchemeConfigDTO> configDTOS = new ArrayList<>()
        configDTOS.add(configDTO);

        when: '创建方案配置'
        HttpEntity<List<StateMachineSchemeConfigDTO>> httpEntity = new HttpEntity<>(configDTOS)
        def entity = restTemplate.exchange(baseUrl + "/create_config/{scheme_id}/{state_machine_id}", HttpMethod.POST, httpEntity, StateMachineSchemeDTO, orginzationId, schemeId, stateMachineId)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        schemeId            | stateMachineId || isSuccess | reponseResult
//        null                | null           || false     | false
        list.get(0).getId() | 1              || true      | true*/
    }

    def "deleteConfig"() {
        given: '创建方案配置'
        StateMachineSchemeConfigDTO configDTO = new StateMachineSchemeConfigDTO();
        configDTO.setIssueTypeId(1L);
        configDTO.setStateMachineId(1L);
        List<StateMachineSchemeConfigDTO> configDTOS = new ArrayList<>()
        configDTOS.add(configDTO);
        schemeService.createSchemeConfig(orginzationId, list.get(0).getId(), configDTOS);

        when: '删除方案配置'
        def entity = restTemplate.exchange(baseUrl + "/delete_config/{scheme_id}/{state_machine_id}", HttpMethod.DELETE, null, StateMachineSchemeDTO, orginzationId, schemeId, stateMachineId)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        schemeId | stateMachineId || isSuccess | reponseResult
//        null                | null           || false     | false
        1        | 1              || true      | true
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

        ParameterizedTypeReference<Boolean> typeRef = new ParameterizedTypeReference<Boolean>() {};
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, orginzationId)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        entity.getBody() != null && entity.getBody() == reponseResult

        where: '测试用例：'
        scheme_id | name       || isSuccess | reponseResult
//        null                | null       || false     | false
        null      | 'name1'    || true      | false
        null      | 'namename' || true      | true
        1         | 'name1'    || true      | true
        1         | 'name2'    || true      | false
        1         | 'testtest' || true      | true
//        list.get(0).getId() | null       || true      | false
    }


}