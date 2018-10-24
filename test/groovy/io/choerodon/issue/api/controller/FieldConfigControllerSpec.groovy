package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.FieldConfigDTO
import io.choerodon.issue.api.dto.FieldConfigDetailDTO
import io.choerodon.issue.api.dto.FieldConfigLineDTO
import io.choerodon.issue.api.dto.FieldDTO
import io.choerodon.issue.api.service.FieldConfigLineService
import io.choerodon.issue.api.service.FieldConfigService
import io.choerodon.issue.api.service.FieldOptionService
import io.choerodon.issue.api.service.FieldService
import io.choerodon.issue.domain.Field
import io.choerodon.issue.domain.FieldConfig
import io.choerodon.issue.domain.FieldConfigLine
import io.choerodon.issue.domain.FieldOption
import io.choerodon.issue.infra.enums.FieldType
import io.choerodon.issue.infra.mapper.FieldConfigLineMapper
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
 * @author shinan.chen
 * @date 2018/8/31
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class FieldConfigControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    FieldConfigService fieldConfigService;

    @Autowired
    FieldService fieldService;

    @Autowired
    FieldOptionService fieldOptionService;

    @Autowired
    FieldConfigLineService fieldConfigLineService;

    @Autowired
    FieldConfigLineMapper fieldConfigLineMapper;

    @Shared
    Long testOrginzationId = 1L;

    @Shared
    List<FieldConfigDTO> list = new ArrayList<>();

    @Shared
    List<FieldConfigLineDTO> list2 = new ArrayList<>();


    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/field_config'

    @Shared
    String baseUrl2 = '/v1/organizations/{organization_id}/field_config_line'

    def setup() {
        println "执行初始化"
        //初始化一个字段
        FieldDTO fieldInput = new FieldDTO()
        fieldInput.setId(1)
        fieldInput.setName("init_name_" + FieldType.INPUT.value())
        fieldInput.setDescription("init_description_" + FieldType.INPUT.value())
        fieldInput.setType(FieldType.INPUT.value())
        fieldInput.setDefaultValue("init_default_value")
        fieldInput.setExtraConfig(null)
        fieldInput.setOrganizationId(testOrginzationId)
        fieldService.create(testOrginzationId, fieldInput)
        // 初始化5条字段配置
        for (int i = 1; i <= 5; i++) {
            FieldConfigDTO fieldConfigDTO = new FieldConfigDTO()
            fieldConfigDTO.setId(i)
            fieldConfigDTO.setName("init_name" + i)
            fieldConfigDTO.setDescription("init_description" + i)
            fieldConfigDTO.setOrganizationId(testOrginzationId)
            fieldConfigDTO = fieldConfigService.create(testOrginzationId, fieldConfigDTO)
            list.add(fieldConfigDTO)
        }
        //创建一个字段，同步到fieldConfigLine中
        FieldDTO fieldInput2 = new FieldDTO()
        fieldInput2.setId(2)
        fieldInput2.setName("init_name2_" + FieldType.INPUT.value())
        fieldInput2.setDescription("init_description_" + FieldType.INPUT.value())
        fieldInput2.setType(FieldType.INPUT.value())
        fieldInput2.setDefaultValue("init_default_value")
        fieldInput2.setExtraConfig(null)
        fieldInput2.setOrganizationId(testOrginzationId)
        fieldService.create(testOrginzationId, fieldInput2)

    }

    def cleanup() {
        FieldConfig del = new FieldConfig()
        fieldConfigService.delete(del);//清空数据
        Field del1 = new Field()
        fieldService.delete(del1);//清空数据
        FieldOption delo = new FieldOption()
        fieldOptionService.delete(delo)
        FieldConfigLine delf = new FieldConfigLine();
        fieldConfigLineService.delete(delf)
        list.clear();
        list2.clear();
    }

    def "create"() {
        given: '准备工作'
        FieldConfigDTO fieldConfigDTO = new FieldConfigDTO();
        fieldConfigDTO.setName(name)
        fieldConfigDTO.setOrganizationId(testOrginzationId);

        when: '创建字段配置'
        HttpEntity<FieldConfigDTO> httpEntity = new HttpEntity<>(fieldConfigDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, FieldConfigDTO, testOrginzationId)

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
        name         || expRequest | expResponse
        'name1'      || true       | true
        null         || true       | false
        'init_name1' || true       | false
    }

    def "update"() {
        given: '准备工作'
        FieldConfigDTO fieldConfigDTO = list.get(0);
        fieldConfigDTO.setName(name);
        fieldConfigDTO.setOrganizationId(testOrginzationId);

        when: '更新字段配置'
        HttpEntity<FieldConfigDTO> httpEntity = new HttpEntity<>(fieldConfigDTO)
        def entity = restTemplate.exchange(baseUrl + '/{id}', HttpMethod.PUT, httpEntity, FieldConfigDTO, testOrginzationId, fieldConfigDTO.getId())

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
        name         || expRequest | expResponse
        'name1'      || true       | true
        null         || true       | false
        'init_name2' || true       | false
    }

    def "delete"() {
        given: '准备工作'
        def fieldConfigId = id

        when: '删除字段配置'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.DELETE, null, Object, testOrginzationId, fieldConfigId)

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

    def "pageQuery"() {
        given: '准备工作'
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
        when: '分页查询'
        ParameterizedTypeReference<Page<FieldConfigDTO>> typeRef = new ParameterizedTypeReference<Page<FieldConfigDTO>>() {
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
        name         | description         | param  || expRequest | expResponseSize
        null         | null                | null   || true       | 5
        'init_name1' | null                | null   || true       | 1
        null         | 'init_description1' | null   || true       | 1
        null         | null                | 'init' || true       | 5
        'notFound'   | null                | null   || true       | 0
        'init'       | 'init'              | 'init' || true       | 5
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

        when: '校验字段配置名字是否未被使用'
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

    def "queryByOrgId"() {
        when: '获取字段配置列表'
        ParameterizedTypeReference<List<FieldConfigDTO>> typeRef = new ParameterizedTypeReference<List<FieldConfigDTO>>() {
        };
        def entity = restTemplate.exchange(baseUrl + "/configs", HttpMethod.GET, null, typeRef, testOrginzationId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponseSize = 0;
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
        true       | 5
    }

    def "queryById"() {
        given: '准备工作'
        def fieldConfigId = id

        when: '根据id查询字段配置'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.GET, null, FieldConfigDetailDTO, testOrginzationId, fieldConfigId)

        then: '状态码为200，调用成功'

        def actRequest = false
        def actResponseSize = 0
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().getId() != null) {
                        if (entity.getBody().getFieldConfigLineDTOList() != null) {
                            actResponseSize = entity.getBody().getFieldConfigLineDTOList().size();
                        }
                    }
                }
            }
        }
        actRequest == expRequest
        actResponseSize == expResponseSize

        where: '测试用例：'
        id     || expRequest | expResponseSize
        '1'    || true       | 2
        '9999' || true       | 0
        null   || true       | 0
    }

    def "updateFieldConfigLine"() {
        given: '准备工作'
        FieldConfigLine fieldConfigLine = fieldConfigLineMapper.selectOne()
        FieldConfigLineDTO fieldConfigLineDTO = new FieldConfigLineDTO();
        fieldConfigLineDTO.setId(fieldConfigLine.getId())
        fieldConfigLineDTO.setFieldId(fieldConfigLine.getFieldId())
        fieldConfigLineDTO.setFieldConfigId(fieldConfigLine.getFieldConfigId())
        fieldConfigLineDTO.setIsRequired(isRequired);
        fieldConfigLineDTO.setIsDisplay(isDisPlayed)
        fieldConfigLineDTO.setObjectVersionNumber(1)

        when: '修改字段配置'
        HttpEntity<FieldConfigLineDTO> httpEntity = new HttpEntity<>(fieldConfigLineDTO)
        def entity = restTemplate.exchange(baseUrl2 + '/{id}', HttpMethod.PUT, httpEntity, FieldConfigLineDTO, testOrginzationId, fieldConfigLineDTO.getId())

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
        isRequired | isDisPlayed || expRequest | expResponse
        '1 '       | '1 '        || true       | true
        null       | '1'         || true       | false
        '1'        | null        || true       | false

    }
}
