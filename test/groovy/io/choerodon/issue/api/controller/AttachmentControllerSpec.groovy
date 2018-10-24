package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.AttachmentDTO
import io.choerodon.issue.api.service.AttachmentService
import io.choerodon.issue.domain.Attachment
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
class AttachmentControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    AttachmentService attachmentService;

    @Shared
    Long testProjectId = 1L;

    @Shared
    List<AttachmentDTO> list = new ArrayList<>();

    @Shared
    String baseUrl = '/v1/projects/{project_id}/attachment'

    def setup() {
        println "执行初始化"
        for (int i = 1; i <= 5; i++) {
            AttachmentDTO attachmentDTO = new AttachmentDTO()
            attachmentDTO.setId(i)
            attachmentDTO.setResourceType("issue")
            attachmentDTO.setResourceId(i)
            attachmentDTO.setUserId(i)
            attachmentDTO.setFileName("init_name" + i)
            attachmentDTO.setFileUrl("init_url" + i)
            attachmentDTO.setFileSize(i)
            attachmentDTO.setProjectId(testProjectId)
            attachmentDTO = attachmentService.create(testProjectId,i,"issue",request,i);
            list.add(attachmentDTO)
        }
    }

    def cleanup() {
        Attachment del = new Attachment()
        attachmentService.delete(del);//清空数据
        list.clear();
    }

    def "create"() {
        given: '准备工作'
        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setUserId(userId);
        attachmentDTO.setFileSize(fileSize);
        attachmentDTO.setFileName(fileName);
        attachmentDTO.setFileUrl(fileUrl);
        attachmentDTO.setResourceType(resourceType);
        attachmentDTO.setResourceId(resourceId);
        attachmentDTO.setProjectId(testProjectId);

        when: '创建附件'
        HttpEntity<AttachmentDTO> httpEntity = new HttpEntity<>(attachmentDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, AttachmentDTO, testProjectId)

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
        resourceType | resourceId | fileName   | fileUrl         | fileSize | userId || expRequest | expResponse
        'issue'      | 1          | 'build.sh' | 'www.baidu.com' | '300'    | 1      || true       | true
        'issue1'     | 1          | 'build.sh' | 'www.baidu.com' | '300'    | 1      || true       | false
        'issue'      | 1          | 'build.sh' | 'www.baidu.com' | '300'    | null   || true       | false
        'issue'      | 1          | 'build.sh' | 'www.baidu.com' | null     | 1      || true       | false
        'issue'      | 1          | 'build.sh' | null            | '300'    | 1      || true       | false
        'issue'      | 1          | null       | 'www.baidu.com' | '300'    | 1      || true       | false
        'issue'      | null       | 'build.sh' | 'www.baidu.com' | '300'    | 1      || true       | false
        null         | 1          | 'build.sh' | 'www.baidu.com' | '300'    | 1      || true       | false
    }

    def "delete"() {
        given: '准备工作'
        def attachmentId = id

        when: '删除事件单回复'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.DELETE, null, Object, testProjectId, attachmentId)

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
//        '9999' || true       | false
//        null   || false      | false
    }


    def "listQuery"() {
        given: '准备工作'
        def url = baseUrl + "/attachment_list?1=1"
        if (param != null) {
            url = url + "&param=" + param
        }
        when: '列表查询'
        ParameterizedTypeReference<List<AttachmentDTO>> typeRef = new ParameterizedTypeReference<List<AttachmentDTO>>() {
        };
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, testProjectId)

        then: '返回结果'
        def actRequest = false
        def actResponseSize = 0
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    actResponseSize = entity.getBody().size();
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
