package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.IssueTypeDTO
import io.choerodon.issue.api.dto.IssueTypeSearchDTO
import io.choerodon.issue.api.service.IssueTypeService
import io.choerodon.issue.domain.IssueType
import io.choerodon.core.domain.Page
import io.choerodon.issue.infra.mapper.IssueTypeMapper
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

/**
 * @author shinan.chen
 * @date 2018/8/13
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueTypeControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    IssueTypeService issueTypeService

    @Autowired
    IssueTypeMapper issueTypeMapper

    @Shared
    Long organizationId = 1L

    @Shared
    List<IssueTypeDTO> list = new ArrayList<>()

    /**
     * 测试编写说明：
     * 1、setup初始化数据执行在每一个单元测试的每一条测试数据之前
     * 2、调用controller方法失败（格式错误等），entity为空，actRequest为false
     * 3、调用成功，执行controller方法时抛出异常，未处理的异常，entity返回值非200，actRequest为false
     * 4、调用成功，执行controller方法时抛出异常，被捕获，entity返回值为200，actRequest为true
     * 5、在path和param中的参数均有null校验，若传null，调用成功，被spring抛出异常，被捕获，entity返回值为200，actRequest为true
     */

    def "create"() {
        given: '准备工作'
        IssueTypeDTO issueTypeDTO = new IssueTypeDTO()
        issueTypeDTO.setName(name)
        issueTypeDTO.setTypeCode(name)
        issueTypeDTO.setIcon(icon)
        issueTypeDTO.setDescription(description)
        issueTypeDTO.setOrganizationId(organizationId)

        when: '创建问题类型'
        HttpEntity<IssueTypeDTO> httpEntity = new HttpEntity<>(issueTypeDTO)
        def entity = restTemplate.exchange("/v1/organizations/{organization_id}/issue_type", HttpMethod.POST, httpEntity, IssueTypeDTO, organizationId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().getId() != null) {
                        actResponse = true
                        list.add(entity.body)
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        name         | icon    | typeCode | description    || expRequest | expResponse
        'name1'      | 'icon1' | 'test'   | 'description1' || true       | true
        null         | 'icon1' | 'test'   | 'description1' || true       | false
        'name1'      | null    | 'test'   | 'description1' || true       | false
        'name11'     | 'icon1' | 'test'   | null           || true       | true
        'init_name1' | 'icon1' | 'test'   | 'description1' || true       | true
    }

    def "update"() {
        given: '准备工作'
        IssueTypeDTO issueTypeDTO = list.get(0)
        issueTypeDTO.setName(name)
        issueTypeDTO.setIcon(icon)
        issueTypeDTO.setDescription(description)
        issueTypeDTO.setOrganizationId(organizationId)
        issueTypeDTO.setObjectVersionNumber(objectVersionNumber)

        when: '更新问题类型'
        HttpEntity<IssueTypeDTO> httpEntity = new HttpEntity<>(issueTypeDTO)
        def entity = restTemplate.exchange('/v1/organizations/{organization_id}/issue_type' + '/{id}', HttpMethod.PUT, httpEntity, IssueTypeDTO, organizationId, issueTypeDTO.getId())

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
        name         | icon    | description    | objectVersionNumber || expRequest | expResponse
        'name1'      | 'icon1' | 'description1' | 1                    | true       | true
        null         | 'icon1' | 'description1' | 1                    | true       | false
        'name1'      | null    | 'description1' | 2                    | true       | true
        'name1'      | 'icon1' | null           | 3                    | true       | true
        'init_name2' | 'icon1' | 'description1' | 3                    | true       | false
    }

    def "checkDelete"() {
        given: '准备工作'
        def issueTypeId = id

        when: '校验问题类型是否可以删除'
        def entity = restTemplate.exchange('/v1/organizations/{organization_id}/issue_type' + "/check_delete/{id}", HttpMethod.GET, null, Map, organizationId, issueTypeId)

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

    def "pageQuery"() {
        given: '准备工作'
        def url = '/v1/organizations/{organization_id}/issue_type/list?page={page}&&size={size}'
        IssueTypeSearchDTO issueTypeSearchDTO = new IssueTypeSearchDTO()
        issueTypeSearchDTO.name = name
        issueTypeSearchDTO.description = description
        issueTypeSearchDTO.param = param
        when: '分页查询'
        ParameterizedTypeReference<Page<IssueTypeDTO>> typeRef = new ParameterizedTypeReference<Page<IssueTypeDTO>>() {}
        HttpEntity<IssueTypeSearchDTO> issueTypeSearchDTOHttpEntity = new HttpEntity<>(issueTypeSearchDTO)
        def entity = restTemplate.exchange(url, HttpMethod.POST, issueTypeSearchDTOHttpEntity, typeRef, organizationId, 0, 10000)

        then: '返回结果'
        def actRequest = false
        def actResponseSize = 0
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    actResponseSize = entity.getBody().size()
                }
            }
        }
        actRequest == expRequest
        actResponseSize == expResponseSize

        where: '测试用例：'
        name         | description         | param  || expRequest | expResponseSize
        null         | null                | null   || true       | 10
        'init_name1' | null                | null   || true       | 1
        null         | 'init_description1' | null   || true       | 0
        null         | null                | 'init' || true       | 1
        'notFound'   | null                | null   || true       | 0
        'init'       | 'init'              | 'init' || true       | 0
    }

    def "checkName"() {
        given: '准备工作'
        def url = '/v1/organizations/{organization_id}/issue_type' + "/check_name?1=1"
        if (name != null) {
            url = url + "&name=" + name
        }
        if (id != null) {
            url = url + "&id=" + id
        }

        when: '校验问题类型名字是否未被使用'
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
        'init_name1' | null || true       | false
        'init_name1' | '1'  || true       | false
        'init_name2' | null || true       | true
    }

    def "queryByOrgId"() {
        when: '获取问题类型列表'
        ParameterizedTypeReference<List<IssueTypeDTO>> typeRef = new ParameterizedTypeReference<List<IssueTypeDTO>>() {
        }
        def entity = restTemplate.exchange('/v1/organizations/{organization_id}/issue_type' + "/types", HttpMethod.GET, null, typeRef, organizationId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponseSize = 0
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    actResponseSize = entity.getBody().size()
                }

            }
        }
        actRequest == expRequest
        actResponseSize == expResponseSize

        where: '测试用例：'
        expRequest | expResponseSize
        true       | 10
    }

    def "queryById"() {
        given: '准备工作'
        def issueTypeId = id

        when: '根据id查询问题类型'
        def entity = restTemplate.exchange('/v1/organizations/{organization_id}/issue_type' + "/{id}", HttpMethod.GET, null, IssueTypeDTO, organizationId, issueTypeId)

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

    def "delete"() {
        given: '准备工作'
        def issueTypeId = issueType.id

        when: '删除问题类型'
        def entity = restTemplate.exchange('/v1/organizations/{organization_id}/issue_type' + "/{id}", HttpMethod.DELETE, null, Object, organizationId, issueTypeId)

        then: '状态码为200，调用成功'
        entity.body == true

        where: ''
        issueType << list

    }
}
