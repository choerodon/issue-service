package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.vo.FieldOptionUpdateVO
import io.choerodon.issue.api.vo.ObjectSchemeFieldCreateVO
import io.choerodon.issue.api.vo.ObjectSchemeFieldDetailVO
import io.choerodon.issue.api.vo.ObjectSchemeFieldUpdateVO
import io.choerodon.issue.app.service.ObjectSchemeFieldService
import io.choerodon.issue.infra.enums.FieldCode
import io.choerodon.issue.infra.enums.FieldType
import io.choerodon.issue.infra.enums.ObjectSchemeCode
import io.choerodon.issue.infra.enums.ObjectSchemeFieldContext
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
 * @since 2019/4/11
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class ObjectSchemeFieldControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate
    @Shared
    Long organizationId = 1L
    @Shared
    ObjectSchemeFieldDetailVO create
    @Autowired
    ObjectSchemeFieldService objectSchemeFieldService

    def url = '/v1/organizations/{organization_id}/object_scheme_field'

    def "listQuery"() {
        given: '准备'
        def testSchemeCode = schemeCode
        when: '根据方案编码获取字段列表'
        def entity = restTemplate.exchange(url + "/list?schemeCode=" + testSchemeCode, HttpMethod.GET, null, Map, organizationId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponseContent = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().get("content") != null && entity.getBody().get("content").size() > 0) {
                        actResponseContent = true
                    }
                }
            }
        }
        actRequest == expRequest
        actResponseContent == expResponseContent

        where: '测试用例：'
        schemeCode                   || expRequest | expResponseContent
        'test'                       || true       | false
        ObjectSchemeCode.AGILE_ISSUE || true       | true
    }

    def "create"() {
        given: '准备'
        ObjectSchemeFieldCreateVO createVO = new ObjectSchemeFieldCreateVO()
        createVO.code = 'csn'
        createVO.context = [ObjectSchemeFieldContext.GLOBAL]
        createVO.description = 'csn'
        createVO.fieldType = FieldType.INPUT
        createVO.name = 'csn'
        createVO.schemeCode = ObjectSchemeCode.AGILE_ISSUE

        when: '创建字段'
        HttpEntity<ObjectSchemeFieldCreateVO> httpEntity = new HttpEntity<>(createVO)
        def entity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, ObjectSchemeFieldDetailVO.class, organizationId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().getId() != null) {
                        actResponse = true
                        create = entity.body
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        expRequest | expResponse
        true       | true
    }

    def "queryById"() {
        given: '准备'
        def fieldId = create.id

        when: '查询字段详情'
        def entity = restTemplate.exchange(url + "/{field_id}", HttpMethod.GET, null, ObjectSchemeFieldDetailVO.class, organizationId, fieldId)

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
        expRequest | expResponse
        true       | true
    }

    def "update"() {
        given: '准备工作'
        ObjectSchemeFieldUpdateVO updateVO = new ObjectSchemeFieldUpdateVO()
        updateVO.name = 'csd'
        updateVO.description = 'csd'
        updateVO.defaultValue = '1'
        updateVO.extraConfig = true
        updateVO.required = true
        updateVO.objectVersionNumber = objectSchemeFieldService.baseQueryById(organizationId, null, create.id).objectVersionNumber
        List<FieldOptionUpdateVO> optionUpdateVOs = new ArrayList<>()
        FieldOptionUpdateVO add = new FieldOptionUpdateVO()
        add.enabled = true
        add.isDefault = true
        add.status = 'add'
        add.value = 'option1'
        optionUpdateVOs.add(add)
        updateVO.fieldOptions = optionUpdateVOs

        when: '更新优先级类型'
        HttpEntity<ObjectSchemeFieldUpdateVO> httpEntity = new HttpEntity<>(updateVO)
        def entity = restTemplate.exchange(url + '/{field_id}', HttpMethod.PUT, httpEntity, ObjectSchemeFieldDetailVO.class, organizationId, create.id)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().getId() != null) {
                        create = entity.getBody()
                        actResponse = true
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        expRequest | expResponse
        true       | true
    }


    def "delete"() {
        given: '准备'
        def fieldId = create.id

        when: '删除字段'
        def entity = restTemplate.exchange(url + "/{field_id}", HttpMethod.DELETE, null, Object, organizationId, fieldId)

        then: '状态码为200，调用成功'
        def actRequest = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
            }
        }
        expect: "期望值"
        actRequest == true
    }

    def "checkName"() {
        given: '准备'
        def url = url + "/check_name?1=1"
        if (name != null) {
            url = url + "&name=" + name
        }

        when: '校验优先级类型名字是否未被使用'
        def entity = restTemplate.exchange(url + "&schemeCode=agile_issue", HttpMethod.GET, null, Boolean.class, organizationId)

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
        name   || expRequest | expResponse
        '问题类型' || true       | true
        'csn'  || true       | false
    }

    def "checkCode"() {
        given: '准备'
        def url = url + "/check_code?1=1"
        if (code != null) {
            url = url + "&code=" + code
        }

        when: '校验优先级类型名字是否未被使用'
        def entity = restTemplate.exchange(url + "&schemeCode=agile_issue", HttpMethod.GET, null, Boolean.class, organizationId)

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
        code                 || expRequest | expResponse
        FieldCode.ISSUE_TYPE || true       | true
        'csn'                || true       | false
    }
}