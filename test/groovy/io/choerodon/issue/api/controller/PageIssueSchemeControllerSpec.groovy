package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.PageIssueTypeSchemeDTO
import io.choerodon.issue.api.service.PageIssueSchemeLineService
import io.choerodon.issue.api.service.PageIssueSchemeService
import io.choerodon.issue.domain.PageIssueScheme
import io.choerodon.core.domain.Page
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
class PageIssueSchemeControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    PageIssueSchemeService schemeService

    @Autowired
    PageIssueSchemeLineService lineService

    @Shared
    Long orginzationId = 1L

    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/page_issue'

    @Shared
    List<PageIssueScheme> list = new ArrayList<>()

    //初始化40条数据
    def setup() {
        def name = 'name'
        def description = 'description'
        def testType = '1'
        for (int i = 1 ;i <= 40 ;i++) {
            PageIssueScheme scheme = new PageIssueScheme()
            scheme.setId(i)
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
        //清空数据
        PageIssueScheme del = new PageIssueScheme()
        schemeService.delete(del)
        list.clear()
    }

    def "pagingQuery"() {
        when: '问题类型页面方案列表'
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
        ParameterizedTypeReference<Page<PageIssueTypeSchemeDTO>> typeRef = new ParameterizedTypeReference<Page<PageIssueTypeSchemeDTO>>() {
        }
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
        given: '创建问题类型页面方案'
        PageIssueTypeSchemeDTO schemeDTO = new PageIssueTypeSchemeDTO()
        schemeDTO.setName(name)
        schemeDTO.setDescription(description)

        when: '状态机方案写入数据库'
        HttpEntity<PageIssueTypeSchemeDTO> httpEntity = new HttpEntity<>(schemeDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, PageIssueTypeSchemeDTO, orginzationId)

        then: '状态码为200，创建成功'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        name         | description         || isSuccess | reponseResult
        'test-name1' | 'test-description1' || true      | true
        null         | 'test-description1' || true      | false
        null         | null                || true      | false
    }

    def "update"() {
        given: '更新问题类型页面方案'
        PageIssueTypeSchemeDTO schemeDTO = new PageIssueTypeSchemeDTO()
        schemeDTO.setName(name)
        schemeDTO.setDescription(description)
        schemeDTO.setObjectVersionNumber(1L)
        schemeDTO.setOrganizationId(orginzationId)
        when: '问题类型页面方案写入数据库'
        HttpEntity<PageIssueTypeSchemeDTO> httpEntity = new HttpEntity<>(schemeDTO)
        def entity = restTemplate.exchange(baseUrl + "/{scheme_id}", HttpMethod.PUT, httpEntity, PageIssueTypeSchemeDTO, orginzationId, schemeId)

        then: '更新成功判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        schemeId | name         | description         || isSuccess | reponseResult
        1        | 'test-name1' | 'test-description1' || true      | true
//        list.get(0).getId() | null         | 'test-description1' || true      | false
//        list.get(0).getId() | null         | null                || true      | false
    }

    def "delete"() {
        when: '删除问题类型页面方案'
        def entity = restTemplate.exchange(baseUrl + "/{scheme_id}", HttpMethod.DELETE, null, Object, orginzationId, schemeId)

        then: '删除结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        if (entity.getBody() != null && entity.getBody() instanceof Boolean) {
            entity.getBody() != null && entity.getBody() == reponseResult
        } else if (entity.getBody() != null) {
            Map map = (Map) entity.getBody()
            map.get("failed") == reponseResult
            map.get("code") == "error.pageIssueScheme.delete"
        }

        where: '测试用例：'
        schemeId || isSuccess | reponseResult
//        null                || true      | false
        1        || true      | true
        99       || true      | false
    }

    def "querySchemeWithConfigById"() {
        when: '根据id查询状态机方案对象'
        def entity = restTemplate.exchange(baseUrl + "/{scheme_id}", HttpMethod.GET, null, PageIssueTypeSchemeDTO, orginzationId, schemeId)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        schemeId || isSuccess | reponseResult
        null     || true      | false
        1        || true      | true
        99       || true      | false
    }


    def "checkName"() {
        when: '校验名字是否未被使用'
        def url = baseUrl + "/check_name?1=1"
        if (scheme_id != null) {
            url = url + "&scheme_id=" + scheme_id
        }
        if (name != null) {
            url = url + "&name=" + name
        }

        ParameterizedTypeReference<Boolean> typeRef = new ParameterizedTypeReference<Boolean>() {}
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