package io.choerodon.issue.api.controller


import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.vo.AdjustOrderVO
import io.choerodon.issue.api.vo.PageFieldUpdateVO
import io.choerodon.issue.api.vo.PageFieldVO
import io.choerodon.issue.infra.enums.PageCode
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
class PageFieldControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    Long organizationId = 1L

    @Shared
    List<PageFieldVO> list

    def url = '/v1/organizations/{organization_id}/page_field'

    def "listQuery"() {
        given: '准备'
        def testPageCode = pageCode
        when: '根据方案编码获取字段列表'
        def entity = restTemplate.exchange(url + "/list?pageCode=" + testPageCode, HttpMethod.GET, null, Map, organizationId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponseContent = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().get("content") != null && entity.getBody().get("content").size() > 0) {
                        actResponseContent = true
                        list = entity.getBody().get("content")
                    }
                }
            }
        }
        actRequest == expRequest
        actResponseContent == expResponseContent

        where: '测试用例：'
        pageCode                    || expRequest | expResponseContent
        'test'                      || true       | false
        PageCode.AGILE_ISSUE_CREATE || true       | true
    }

    def "adjustFieldOrder"() {
        given: '准备'
        def testPageCode = PageCode.AGILE_ISSUE_CREATE
        AdjustOrderVO adjust = new AdjustOrderVO()
        adjust.before = true
        adjust.currentFieldId = list[1].fieldId
        adjust.outsetFieldId = list[0].fieldId

        when: '调整顺序'
        HttpEntity<AdjustOrderVO> httpEntity = new HttpEntity<>(adjust)
        def entity = restTemplate.exchange(url + "/adjust_order?pageCode=" + testPageCode, HttpMethod.POST, httpEntity, PageFieldVO.class, organizationId)

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
        def testPageCode = PageCode.AGILE_ISSUE_CREATE
        PageFieldUpdateVO updateVO = new PageFieldUpdateVO()
        updateVO.objectVersionNumber = list[2].objectVersionNumber
        updateVO.display = true

        when: '更新优先级类型'
        HttpEntity<PageFieldUpdateVO> httpEntity = new HttpEntity<>(updateVO)
        def entity = restTemplate.exchange(url + '/{field_id}?pageCode=' + testPageCode, HttpMethod.PUT, httpEntity, PageFieldVO.class, organizationId, list[2].fieldId)

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
}