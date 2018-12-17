package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.IssueReplyDTO
import io.choerodon.issue.api.service.IssueReplyService
import io.choerodon.issue.domain.IssueReply
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
 * @author jiameng.cao
 * @date 2018/9/4
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueReplyControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    IssueReplyService issueReplyService

    @Shared
    Long testProjectId = 1L

    @Shared
    List<IssueReplyDTO> list = new ArrayList<>()

    @Shared
    String baseUrl = '/v1/projects/{project_id}/issue_reply'

    def setup() {
        println "执行初始化"
        for (int i = 1; i <= 5; i++) {
            IssueReplyDTO issueReplyDTO = new IssueReplyDTO()
            issueReplyDTO.setId(i)
            issueReplyDTO.setUserId(i)
            issueReplyDTO.setContent("init_content" + i)
            issueReplyDTO.setIssueId(i)
            issueReplyDTO.setSourceReplyId(i)
            issueReplyDTO.setProjectId(testProjectId)
            issueReplyDTO = issueReplyService.create(testProjectId, issueReplyDTO)
            list.add(issueReplyDTO)
        }
    }

    def cleanup() {
        IssueReply del = new IssueReply()
        issueReplyService.delete(del)//清空数据
        list.clear()
    }

    def "create"() {
        given: '准备工作'
        IssueReplyDTO issueReplyDTO = new IssueReplyDTO()
        issueReplyDTO.setUserId(userId)
        issueReplyDTO.setContent(content)
        issueReplyDTO.setIssueId(issueId)
        issueReplyDTO.setSourceReplyId(sourceReplyId)
        issueReplyDTO.setProjectId(testProjectId)

        when: '创建事件单回复'
        HttpEntity<IssueReplyDTO> httpEntity = new HttpEntity<>(issueReplyDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, IssueReplyDTO, testProjectId)

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
        userId | content    | issueId | sourceReplyId || expRequest | expResponse
        1      | 'content1' | 1       | 1             || true       | true
        1      | 'content1' | 1       | null          || true       | false
        1      | 'content1' | null    | 1             || true       | false
        1      | null       | 1       | 1             || true       | false
        null   | 'content1' | 1       | 1             || true       | false
    }

    def "update"() {
        given: '准备工作'
        IssueReplyDTO issueReplyDTO = list.get(0)
        issueReplyDTO.setUserId(userId)
        issueReplyDTO.setContent(content)
        issueReplyDTO.setIssueId(issueId)
        issueReplyDTO.setSourceReplyId(sourceReplyId)
        issueReplyDTO.setProjectId(testProjectId)


        when: '修改事件单回复'
        HttpEntity<IssueReplyDTO> httpEntity = new HttpEntity<>(issueReplyDTO)
        def entity = restTemplate.exchange(baseUrl + '/{id}', HttpMethod.PUT, httpEntity, IssueReplyDTO, testProjectId, issueReplyDTO.getId())

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
        userId | content    | issueId | sourceReplyId || expRequest | expResponse
        2      | 'content1' | 2       | 2             || true       | true
        1      | 'content1' | 1       | null          || true       | false
        1      | 'content1' | null    | 1             || true       | false
        1      | null       | 1       | 1             || true       | false
        null   | 'content1' | 1       | 1             || true       | false
    }

    def "delete"() {
        given: '准备工作'
        def issueReplyId = id

        when: '删除事件单回复'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.DELETE, null, Object, testProjectId, issueReplyId)

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
        id     || expRequest | expResponse
        '1'    || true       | true
        '9999' || true       | false
        null   || false      | false
    }

    def "listQuery"() {
        given: '准备工作'
        def url = baseUrl + "/issue_reply_list?1=1"
        if (param != null) {
            url = url + "&param=" + param
        }
        when: '列表查询'
        ParameterizedTypeReference<List<IssueReplyDTO>> typeRef = new ParameterizedTypeReference<List<IssueReplyDTO>>() {
        }
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, testProjectId)

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
        param || expRequest | expResponseSize
        null  || true       | 5
        '1'   || true       | 1
    }
}
