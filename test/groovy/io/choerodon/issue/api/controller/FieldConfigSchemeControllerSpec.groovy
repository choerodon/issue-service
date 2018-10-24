package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.FieldConfigSchemeDTO
import io.choerodon.issue.api.dto.FieldConfigSchemeDetailDTO
import io.choerodon.issue.api.dto.FieldConfigSchemeLineDTO
import io.choerodon.issue.api.service.FieldConfigSchemeLineService
import io.choerodon.issue.api.service.FieldConfigSchemeService
import io.choerodon.issue.domain.FieldConfigScheme
import io.choerodon.issue.domain.FieldConfigSchemeLine
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

/**
 * @author jiameng.cao
 * @date 2018/8/31
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class FieldConfigSchemeControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    FieldConfigSchemeService fieldConfigSchemeService;

    @Autowired
    FieldConfigSchemeLineService fieldConfigSchemeLineService;

    @Shared
    Long testOrginzationId = 1L;

    @Shared
    List<FieldConfigSchemeDetailDTO> list1 = new ArrayList<>();

    @Shared
    List<FieldConfigSchemeDTO> list2 = new ArrayList<>();

    @Shared
    List<FieldConfigSchemeLineDTO> lineDTOList = new ArrayList<>();

    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/field_config_scheme'

    //初始化5条数据
    def setup() {
        println "执行初始化"
        for (int i = 1; i <= 5; i++) {
            FieldConfigSchemeLineDTO fieldConfigSchemeLineDTO = new FieldConfigSchemeLineDTO()
            fieldConfigSchemeLineDTO.setSchemeId(i)
            fieldConfigSchemeLineDTO.setFieldConfigId(i)
            fieldConfigSchemeLineDTO.setIssueTypeId(i)
            lineDTOList.add(fieldConfigSchemeLineDTO)
        }
        for (int i = 1; i <= 5; i++) {
            FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO = new FieldConfigSchemeDetailDTO()
            fieldConfigSchemeDetailDTO.setId(i)
            fieldConfigSchemeDetailDTO.setName("init_name" + i)
            fieldConfigSchemeDetailDTO.setDescription("init_description" + i)
            fieldConfigSchemeDetailDTO.setOrganizationId(testOrginzationId)
            fieldConfigSchemeDetailDTO.setFieldConfigSchemeLineDTOList(lineDTOList)
            fieldConfigSchemeDetailDTO = fieldConfigSchemeService.create(testOrginzationId, fieldConfigSchemeDetailDTO)
            list1.add(fieldConfigSchemeDetailDTO)
        }
    }

    def cleanup() {
        FieldConfigScheme del = new FieldConfigScheme()
        fieldConfigSchemeService.delete(del);//清空数据
        FieldConfigSchemeLine del2 = new FieldConfigSchemeLine()
        fieldConfigSchemeLineService.delete(del2);
        list1.clear();
        list2.clear();
        lineDTOList.clear();
    }

    def "create"() {
        given: '准备工作'
        FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO = new FieldConfigSchemeDetailDTO();
        fieldConfigSchemeDetailDTO.setName(name);
        fieldConfigSchemeDetailDTO.setDescription(description);
        fieldConfigSchemeDetailDTO.setOrganizationId(testOrginzationId);

        FieldConfigSchemeLineDTO fieldConfigSchemeLineDTO = new FieldConfigSchemeLineDTO();
        fieldConfigSchemeLineDTO.setSchemeId(schemeId)
        fieldConfigSchemeLineDTO.setIssueTypeId(issueTypeId)
        fieldConfigSchemeLineDTO.setFieldConfigId(fieldConfigId)
        fieldConfigSchemeDetailDTO.setFieldConfigSchemeLineDTOList(Arrays.asList(fieldConfigSchemeLineDTO))

        when: '创建字段配置方案'
        HttpEntity<FieldConfigSchemeDetailDTO> httpEntity = new HttpEntity<>(fieldConfigSchemeDetailDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, FieldConfigSchemeDetailDTO, testOrginzationId)

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
        name         | description    | schemeId | issueTypeId | fieldConfigId || expRequest | expResponse
        'name1'      | 'description1' | 4        | 4           | 4             || true       | true
        'name1'      | 'description1' | 4        | 4           | null          || true       | false  //fieldConfigId 不存在
        'name1'      | 'description1' | 4        | null        | 4             || true       | false
        'name1'      | 'description1' | null     | 4           | 4             || true       | true
        'name1'      | null           | 4        | 4           | 4             || true       | true
        null         | 'description1' | 4        | 4           | 4             || true       | false
        'init_name1' | 'description1' | 4        | 4           | 4             || true       | false

    }

    def "update"() {
        given: '准备工作'
        FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO = list1.get(0);
        fieldConfigSchemeDetailDTO.setName(name);
        fieldConfigSchemeDetailDTO.setDescription(description);
        fieldConfigSchemeDetailDTO.setOrganizationId(testOrginzationId);

        FieldConfigSchemeLineDTO fieldConfigSchemeLineDTO = new FieldConfigSchemeLineDTO();
        fieldConfigSchemeLineDTO.setSchemeId(schemeId)
        fieldConfigSchemeLineDTO.setIssueTypeId(issueTypeId)
        fieldConfigSchemeLineDTO.setFieldConfigId(fieldConfigId)
        fieldConfigSchemeDetailDTO.setFieldConfigSchemeLineDTOList(Arrays.asList(fieldConfigSchemeLineDTO))

        when: '更新字段配置方案'
        HttpEntity<FieldConfigSchemeDetailDTO> httpEntity = new HttpEntity<>(fieldConfigSchemeDetailDTO)
        def entity = restTemplate.exchange(baseUrl + '/{id}', HttpMethod.PUT, httpEntity, FieldConfigSchemeDetailDTO, testOrginzationId, fieldConfigSchemeDetailDTO.getId())

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
        name    | description   | schemeId | issueTypeId | fieldConfigId || expRequest | expResponse
        'name1' | 'description' | 4        | 1           | 4             || true       | true
        null    | 'description' | 4        | 1           | 4             || true       | false
        'name1' | null          | 4        | 1           | 4             || true       | true
        'name1' | 'description' | 4        | null        | 4             || true       | false

    }

    def "delete"() {
        given: '准备工作'
        def fieldConfigId = id

        when: '删除字段配置方案'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.DELETE, null, Object, testOrginzationId, fieldConfigId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null && entity.getBody() instanceof Boolean) {
                    actResponse = entity.getBody();
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        id   || expRequest | expResponse
        1    || true       | true
        9    || true       | false
        null || false      | false
    }


    def "pageQuery"() {
        given: '准备工作'
        def url = baseUrl + "?1=1"
        if (name != null) {
            url = url + "&name=" + name
        }
        if (param != null) {
            url = url + "&param=" + param
        }
        when: '分页查询'
        ParameterizedTypeReference<Page<FieldConfigSchemeDTO>> typeRef = new ParameterizedTypeReference<Page<FieldConfigSchemeDTO>>() {
        };
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, testOrginzationId)

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
        name         | param  || expRequest | expResponseSize
        null         | null   || true       | 5
        'init_name1' | null   || true       | 1
        null         | 'init' || true       | 5
        'notFound'   | null   || true       | 0
        'init'       | 'init' || true       | 5
    }

    def "checkName"() {
        given: '准备工作'
        def url = baseUrl + "/check_name?1=1"
        if (name != null) {
            url = url + "&name=" + name
        }
        if (id != null) {
            url = url + "&id=" + id
        }

        when: '校验字段配置方案名字是否未被使用'
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, Boolean.class, testOrginzationId)

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
        'init_name1' | '1'  || true       | true
        'name1'      | null || true       | true
    }

    def "queryById"() {
        given: '准备工作'
        def schemeId = id

        when: '根据id查询问题类型方案'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.GET, null, FieldConfigSchemeDetailDTO, testOrginzationId, schemeId)

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
        id   || expRequest | expResponse
        1    || true       | true
        7    || true       | false
        null || true       | false
    }

}
