package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.IssueTypeDTO
import io.choerodon.issue.api.dto.IssueTypeSchemeDTO
import io.choerodon.issue.api.dto.IssueTypeSchemeSearchDTO
import io.choerodon.issue.api.service.IssueTypeSchemeService
import io.choerodon.issue.api.service.IssueTypeService
import com.github.pagehelper.PageInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise


/**
 * @author shinan.chen
 * @date 2018/8/16
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueTypeSchemeControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    IssueTypeService issueTypeService

    @Autowired
    IssueTypeSchemeService issueTypeSchemeService

    @Shared
    Long organizationId = 1L

    @Shared
    List<IssueTypeSchemeDTO> list = new ArrayList<>()

    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/issue_type_scheme'

    def "issueTypeSchemeInitData"() {
        given: '初始化数据'
        IssueTypeSchemeDTO issueTypeSchemeDTO = new IssueTypeSchemeDTO()
        issueTypeSchemeDTO.setName("init_name")
        issueTypeSchemeDTO.setDefaultIssueTypeId(1L)
        issueTypeSchemeDTO.setDescription("init_description")
        issueTypeSchemeDTO.setOrganizationId(organizationId)
        issueTypeSchemeDTO.setIssueTypes(issueTypeService.queryByOrgId(organizationId))
        issueTypeSchemeDTO = issueTypeSchemeService.create(organizationId, issueTypeSchemeDTO)
        list.add(issueTypeSchemeDTO)
    }

    def "create"() {
        given: '准备工作'
        IssueTypeSchemeDTO issueTypeSchemeDTO = new IssueTypeSchemeDTO()
        issueTypeSchemeDTO.setName(name)
        issueTypeSchemeDTO.setDefaultIssueTypeId(defaultIssueTypeId)
        issueTypeSchemeDTO.setDescription(description)
        issueTypeSchemeDTO.setOrganizationId(organizationId)

        IssueTypeDTO issueTypeDTO = new IssueTypeDTO()
        issueTypeDTO.setName(configName)
        issueTypeDTO.setId(configId)
        issueTypeSchemeDTO.setIssueTypes(Arrays.asList(issueTypeDTO))

        when: '创建问题类型方案'
        HttpEntity<IssueTypeSchemeDTO> httpEntity = new HttpEntity<>(issueTypeSchemeDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, IssueTypeSchemeDTO, organizationId)

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
        name    | defaultIssueTypeId | description    | configId | configName   || expRequest | expResponse
        'name1' | 1L                 | 'description1' | 1L       | 'init_name1' || true       | true
        'name1' | 1L                 | 'description1' | 9999L    | 'init_name1' || true       | false  //configid 不存在
        'name1' | 1L                 | 'description1' | null     | 'init_name1' || true       | false   //configid null
        null    | 1L                 | 'description1' | 1L       | 'init_name1' || true       | false    //name null
        'name1' | null               | 'description1' | 1L       | 'init_name1' || true       | false    //default非空
//        'init_name1' | 1L                 | 'description1' | 1L       | 'init_name1' || true       | false  //name重名
    }

    def "update"() {
        given: '准备工作'
        IssueTypeSchemeDTO issueTypeSchemeDTO = list.get(0)
        issueTypeSchemeDTO.setName(name)
        issueTypeSchemeDTO.setDefaultIssueTypeId(defaultIssueTypeId)
        issueTypeSchemeDTO.setOrganizationId(organizationId)
        issueTypeSchemeDTO.setObjectVersionNumber(1L)
        IssueTypeDTO issueTypeDTO = new IssueTypeDTO()
        issueTypeDTO.setId(configId)
        issueTypeSchemeDTO.setIssueTypes(Arrays.asList(issueTypeDTO))

        when: '更新问题类型方案'
        HttpEntity<IssueTypeSchemeDTO> httpEntity = new HttpEntity<>(issueTypeSchemeDTO)
        def entity = restTemplate.exchange(baseUrl + '/{id}', HttpMethod.PUT, httpEntity, IssueTypeSchemeDTO, organizationId, issueTypeSchemeDTO.getId())

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
        name    | defaultIssueTypeId | configId || expRequest | expResponse
        'name1' | 1L                 | 1L       || true       | false  //name重名
        'name2' | 1L                 | 1L       || true       | true
        'name2' | 1L                 | 9999L    || true       | false  //configid 不存在
        'name2' | 1L                 | null     || true       | false   //configid null
        null    | 1L                 | 1L       || true       | false    //name null
        'name2' | null               | 1L       || true       | false    //default非空
    }

    def "checkDelete"() {
        given: '准备工作'
        def issueTypeSchemeId = id

        when: '校验问题类型方案是否可以删除'
        def entity = restTemplate.exchange(baseUrl + "/check_delete/{id}", HttpMethod.GET, null, Map, organizationId, issueTypeSchemeId)

        then: '状态码为200，调用成功'

        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().get('canDelete') != null) {
                        actResponse = entity.getBody().get('canDelete')
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        id     || expRequest | expResponse
        '1'    || true       | true
        '9999' || true       | false
        null   || false      | false
    }

//    def "pageQuery"() {
//        given: '准备工作'
//        def url = baseUrl + "?1=1"
//        if (name != null) {
//            url = url + "&name=" + name
//        }
//        if (description != null) {
//            url = url + "&description=" + description
//        }
//        if (param != null) {
//            url = url + "&param=" + param
//        }
//        when: '分页查询'
//        ParameterizedTypeReference<PageInfo<IssueTypeSchemeDTO>> typeRef = new ParameterizedTypeReference<PageInfo<IssueTypeSchemeDTO>>() {
//        }
//        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, organizationId)
//
//        then: '返回结果'
//        def actRequest = false
//        def actResponseSize = 0
//        if (entity != null) {
//            if (entity.getStatusCode().is2xxSuccessful()) {
//                actRequest = true
//                if (entity.getBody() != null) {
//                    actResponseSize = entity.getBody().size()
//                }
//            }
//        }
//        actRequest == expRequest
//        actResponseSize == expResponseSize
//
//        where: '测试用例：'
//        name         | description         | param  || expRequest | expResponseSize
//        null         | null                | null   || true       | 5
//        'init_name1' | null                | null   || true       | 1
//        null         | 'init_description1' | null   || true       | 1
//        null         | null                | 'init' || true       | 5
//        'notFound'   | null                | null   || true       | 0
//        'init'       | 'init'              | 'init' || true       | 5
//    }

    def "checkName"() {
        given: '准备工作'
        def url = baseUrl + "/check_name?1=1"
        if (name != null) {
            url = url + "&name=" + name
        }
        if (id != null) {
            url = url + "&id=" + id
        }

        when: '校验问题类型方案名字是否未被使用'
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, Boolean.class, organizationId)

        then: '状态码为200，调用成功'

        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                actResponse = entity.getBody()
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        name         | id   || expRequest | expResponse
        'init_name1' | null || true       | true
        'name2'      | null || true       | false
    }

    def "queryById"() {
        given: '准备工作'
        def issueTypeSchemeId = id

        when: '根据id查询问题类型方案'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.GET, null, IssueTypeSchemeDTO, organizationId, issueTypeSchemeId)

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
        id     || expRequest | expResponse
        '1'    || true       | true
        '9999' || true       | false
        null   || false      | false
    }

    def "queryIssueTypeSchemeList"() {
        given: '准备数据'
        IssueTypeSchemeSearchDTO issueTypeSchemeSearchDTO = new IssueTypeSchemeSearchDTO()
        issueTypeSchemeSearchDTO.description = "XX"
        issueTypeSchemeSearchDTO.name = "XX"
        issueTypeSchemeSearchDTO.param = "XX"
        HttpEntity<IssueTypeSchemeSearchDTO> httpEntity = new HttpEntity<>(issueTypeSchemeSearchDTO)
        when: '分页查询问题类型方案列表'
        def entity = restTemplate.exchange("/v1/organizations/{organization_id}/issue_type_scheme/list?page={page}&&size={size}", HttpMethod.POST, httpEntity, PageInfo, organizationId, 1, 1000)

        then: '状态码为200，调用成功'
        entity.statusCode.is2xxSuccessful()

        expect: '测试用例：'
        entity.body.getList().size() == 0
    }

    def "delete"() {
        given: '准备工作'
        def issueTypeSchemeId = issueTypeSchemeDTO.id

        when: '删除问题类型方案'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.DELETE, null, Object, organizationId, issueTypeSchemeId)

        then: '状态码为200，调用成功'
        entity.body

        where: '测试用例：'

        issueTypeSchemeDTO << list
    }
}
