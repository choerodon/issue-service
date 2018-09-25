package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.FieldDTO
import io.choerodon.issue.api.dto.FieldDetailDTO
import io.choerodon.issue.api.dto.FieldOptionDTO
import io.choerodon.issue.api.dto.PageDetailDTO
import io.choerodon.issue.api.service.FieldOptionService
import io.choerodon.issue.api.service.FieldService
import io.choerodon.issue.api.service.PageService
import io.choerodon.issue.domain.Field
import io.choerodon.issue.domain.FieldOption
import io.choerodon.issue.infra.enums.FieldType
import io.choerodon.issue.infra.mapper.FieldOptionMapper
import io.choerodon.core.domain.Page
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author shinan.chen
 * @date 2018/8/30
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class FiledControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    FieldService fieldService;

    @Autowired
    FieldOptionService fieldOptionService;

    @Autowired
    PageService pageService;

    @Autowired
    FieldOptionMapper fieldOptionMapper;

    @Shared
    Long testOrginzationId = 1L;

    @Shared
    List<Field> list = new ArrayList<>();

    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/field'

    @Shared
    String baseUrl2 = '/v1/organizations/{organization_id}/field_option'

    //初始化12条数据
    def setup() {
        println "执行初始化"
        //初始化 选择器（级联选择）
        FieldDTO fieldCascade = new FieldDTO()
        fieldCascade.setId(1)
        fieldCascade.setName("init_name_" + FieldType.CASCADE.value())
        fieldCascade.setDescription("init_description_" + FieldType.CASCADE.value())
        fieldCascade.setType(FieldType.CASCADE.value())
        fieldCascade.setDefaultValue(null)
        fieldCascade.setExtraConfig(null)
        fieldCascade.setOrganizationId(testOrginzationId)
        fieldCascade = fieldService.create(testOrginzationId, fieldCascade)
        list.add(fieldCascade)

        //初始化 复选框
        FieldDTO fieldCheckbox = new FieldDTO()
        fieldCheckbox.setId(2)
        fieldCheckbox.setName("init_name_" + FieldType.CHECKBOX.value())
        fieldCheckbox.setDescription("init_description_" + FieldType.CHECKBOX.value())
        fieldCheckbox.setType(FieldType.CHECKBOX.value())
        fieldCheckbox.setDefaultValue(null)
        fieldCheckbox.setExtraConfig(null)
        fieldCheckbox.setOrganizationId(testOrginzationId)
        fieldCheckbox = fieldService.create(testOrginzationId, fieldCheckbox)
        list.add(fieldCheckbox)

        //初始化 日期时间选择器
        FieldDTO fieldDatetime = new FieldDTO()
        fieldDatetime.setId(3)
        fieldDatetime.setName("init_name_" + FieldType.DATETIME.value())
        fieldDatetime.setDescription("init_description_" + FieldType.DATETIME.value())
        fieldDatetime.setType(FieldType.DATETIME.value())
        fieldDatetime.setDefaultValue("2018-01-01 12:12:12")
        fieldDatetime.setExtraConfig("1") //是否设置当前时间为默认日期
        fieldDatetime.setOrganizationId(testOrginzationId)
        fieldDatetime = fieldService.create(testOrginzationId, fieldDatetime)
        list.add(fieldDatetime)

        //初始化 文本框（单行）
        FieldDTO fieldInput = new FieldDTO()
        fieldInput.setId(4)
        fieldInput.setName("init_name_" + FieldType.INPUT.value())
        fieldInput.setDescription("init_description_" + FieldType.INPUT.value())
        fieldInput.setType(FieldType.INPUT.value())
        fieldInput.setDefaultValue("init_default_value")
        fieldInput.setExtraConfig(null)
        fieldInput.setOrganizationId(testOrginzationId)
        fieldInput = fieldService.create(testOrginzationId, fieldInput)
        list.add(fieldInput)

        //初始化 标签
        FieldDTO fieldLabel = new FieldDTO()
        fieldLabel.setId(5)
        fieldLabel.setName("init_name_" + FieldType.LABEL.value())
        fieldLabel.setDescription("init_description_" + FieldType.LABEL.value())
        fieldLabel.setType(FieldType.LABEL.value())
        fieldLabel.setDefaultValue("init_default_value")//用逗号隔开
        fieldLabel.setExtraConfig(null)
        fieldLabel.setOrganizationId(testOrginzationId)
        fieldLabel = fieldService.create(testOrginzationId, fieldLabel)
        list.add(fieldLabel)

        //初始化 选择器（多选）
        FieldDTO fieldMultiple = new FieldDTO()
        fieldMultiple.setId(6)
        fieldMultiple.setName("init_name_" + FieldType.MULTIPLE.value())
        fieldMultiple.setDescription("init_description_" + FieldType.MULTIPLE.value())
        fieldMultiple.setType(FieldType.MULTIPLE.value())
        fieldMultiple.setDefaultValue(null)
        fieldMultiple.setExtraConfig(null)
        fieldMultiple.setOrganizationId(testOrginzationId)
        fieldService.create(testOrginzationId, fieldMultiple)
        list.add(fieldMultiple)

        FieldDetailDTO fieldDetailDTO = fieldService.queryById(testOrginzationId, fieldMultiple.getId());
        //更新字段选项
        FieldOption fieldOption = new FieldOption();
        fieldOption.setId(1)
        fieldOption.setFieldId(fieldDetailDTO.getId());
        fieldOption.setParentId(0L)
        fieldOption.setIsEnable("1")
        fieldOption.setIsDefault("1")
        fieldOption.setSequence(BigDecimal.valueOf(0))
        fieldOption.setValue("optionValue1")
        fieldOptionMapper.insert(fieldOption)

        //初始化 数字输入框
        FieldDTO fieldNumber = new FieldDTO()
        fieldNumber.setId(7)
        fieldNumber.setName("init_name_" + FieldType.NUMBER.value())
        fieldNumber.setDescription("init_description_" + FieldType.NUMBER.value())
        fieldNumber.setType(FieldType.NUMBER.value())
        fieldNumber.setDefaultValue("0")
        fieldNumber.setExtraConfig("0") //是否小数
        fieldNumber.setOrganizationId(testOrginzationId)
        fieldNumber = fieldService.create(testOrginzationId, fieldNumber)
        list.add(fieldNumber)

        //初始化 单选框
        FieldDTO fieldRadio = new FieldDTO()
        fieldRadio.setId(8)
        fieldRadio.setName("init_name_" + FieldType.RADIO.value())
        fieldRadio.setDescription("init_description_" + FieldType.RADIO.value())
        fieldRadio.setType(FieldType.RADIO.value())
        fieldRadio.setDefaultValue(null)
        fieldRadio.setExtraConfig(null)
        fieldRadio.setOrganizationId(testOrginzationId)
        fieldRadio = fieldService.create(testOrginzationId, fieldRadio)
        list.add(fieldRadio)

        //初始化 选择器（单选）
        FieldDTO fieldSingle = new FieldDTO()
        fieldSingle.setId(9)
        fieldSingle.setName("init_name_" + FieldType.SINGLE.value())
        fieldSingle.setDescription("init_description_" + FieldType.SINGLE.value())
        fieldSingle.setType(FieldType.SINGLE.value())
        fieldSingle.setDefaultValue(null)
        fieldSingle.setExtraConfig(null)
        fieldSingle.setOrganizationId(testOrginzationId)
        fieldSingle = fieldService.create(testOrginzationId, fieldSingle)
        list.add(fieldSingle)

        //初始化 文本框（多行）
        FieldDTO fieldText = new FieldDTO()
        fieldText.setId(10)
        fieldText.setName("init_name_" + FieldType.TEXT.value())
        fieldText.setDescription("init_description_" + FieldType.TEXT.value())
        fieldText.setType(FieldType.TEXT.value())
        fieldText.setDefaultValue(null)
        fieldText.setExtraConfig(null)
        fieldText.setOrganizationId(testOrginzationId)
        fieldText = fieldService.create(testOrginzationId, fieldText)
        list.add(fieldText)

        //初始化 时间选择器
        FieldDTO fieldTime = new FieldDTO()
        fieldTime.setId(11)
        fieldTime.setName("init_name_" + FieldType.TIME.value())
        fieldTime.setDescription("init_description_" + FieldType.TIME.value())
        fieldTime.setType(FieldType.TIME.value())
        fieldTime.setDefaultValue("12:12:12")
        fieldTime.setExtraConfig("1") //是否设置当前时间为默认日期
        fieldTime.setOrganizationId(testOrginzationId)
        fieldTime = fieldService.create(testOrginzationId, fieldTime)
        list.add(fieldTime)

        //初始化 URL
        FieldDTO fieldUrl = new FieldDTO()
        fieldUrl.setId(12)
        fieldUrl.setName("init_name_" + FieldType.URL)
        fieldUrl.setDescription("init_description_" + FieldType.URL.value())
        fieldUrl.setType(FieldType.URL.value())
        fieldUrl.setDefaultValue(null)
        fieldUrl.setExtraConfig(null)
        fieldUrl.setOrganizationId(testOrginzationId)
        fieldUrl = fieldService.create(testOrginzationId, fieldUrl)
        list.add(fieldUrl)
    }

    def cleanup() {
        Field del = new Field()
        fieldService.delete(del);//清空数据
        FieldOption delo = new FieldOption()
        fieldOptionService.delete(delo)
        list.clear();
    }

    def "create"() {
        given: '准备工作'
        FieldDTO fieldDTO = new FieldDTO();
        fieldDTO.setName(name)
        fieldDTO.setType(type)
        fieldDTO.setDefaultValue(defaultValue)
        fieldDTO.setExtraConfig(extraConfig)
        fieldDTO.setOrganizationId(testOrginzationId)

        when: '创建字段'
        HttpEntity<FieldDTO> httpEntity = new HttpEntity<>(fieldDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, FieldDTO, testOrginzationId)

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
        name             | type       | defaultValue        | extraConfig || expRequest | expResponse
        'name1'          | 'text'     | null                | null        || true       | true
        'init_name_text' | 'text'     | null                | null        || true       | false
        'name1'          | 'null'     | '1'                 | '1'         || true       | false
        'name1'          | 'radio'    | null                | null        || true       | true
        'name1'          | 'checkbox' | null                | null        || true       | true
        'name1'          | 'time'     | null                | '1'         || true       | true
        'name1'          | 'datetime' | null                | '1'         || true       | true
        'name1'          | 'number'   | '0'                 | '0'         || true       | true
        'name1'          | 'input'    | 'default'           | null        || true       | true
        'name1'          | 'single'   | null                | null        || true       | true
        'name1'          | 'multiple' | null                | null        || true       | true
        'name1'          | 'cascade'  | null                | null        || true       | true
        'name1'          | 'url'      | null                | null        || true       | true
        'name1'          | 'label'    | 'default1,default2' | null        || true       | true
    }

    def "update"() {
        given: '准备工作'
        FieldDetailDTO fieldMultiple = fieldService.queryById(testOrginzationId, 6);

        FieldOptionDTO fieldOptionDTO1 = new FieldOptionDTO();
        fieldOptionDTO1.setFieldId(fieldMultiple.getId());
        fieldOptionDTO1.setId(fieldOptionId1)
        fieldOptionDTO1.setValue(fieldOptionValue1)
        fieldOptionDTO1.setParentId(fieldOptionParentId1)
        fieldOptionDTO1.setObjectVersionNumber(fieldOptionObjectVersionNumber1)

        FieldOptionDTO fieldOptionDTO2 = new FieldOptionDTO();
        fieldOptionDTO2.setFieldId(fieldMultiple.getId());
        fieldOptionDTO2.setId(fieldOptionId2)
        fieldOptionDTO2.setValue(fieldOptionValue2)
        fieldOptionDTO2.setParentId(fieldOptionParentId2)
        fieldOptionDTO2.setObjectVersionNumber(fieldOptionObjectVersionNumber2)
        fieldMultiple.setFieldOptions(Arrays.asList(fieldOptionDTO1, fieldOptionDTO2));

        when: '更新字段'
        HttpEntity<FieldDetailDTO> httpEntity = new HttpEntity<>(fieldMultiple)
        def entity = restTemplate.exchange(baseUrl + '/{id}', HttpMethod.PUT, httpEntity, FieldDetailDTO, testOrginzationId, fieldMultiple.getId())

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
        fieldOptionId1 | fieldOptionValue1 | fieldOptionParentId1 | fieldOptionObjectVersionNumber1 | fieldOptionId2 | fieldOptionValue2 | fieldOptionParentId2 | fieldOptionObjectVersionNumber2 || expRequest | expResponse
        1              | "optionValue1"    | null                 | 1                               | null           | "optionValue2"    | null                 | null                            || true       | true
        1              | "optionValue1"    | null                 | 1                               | null           | "optionValue1"    | 0                    | null                            || true       | false
        0              | "optionValue1"    | null                 | 0                               | null           | "optionValue2"    | 0                    | null                            || true       | true
    }

    def "delete"() {
        given: '准备工作'
        def fieldId = id

        when: '删除字段'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.DELETE, null, Object, testOrginzationId, fieldId)

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
        ParameterizedTypeReference<Page<FieldDTO>> typeRef = new ParameterizedTypeReference<Page<FieldDTO>>() {
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
        name             | description | param  || expRequest | expResponseSize
        null             | null        | null   || true       | 12
        'init_name_text' | null        | null   || true       | 1
        null             | null        | 'init' || true       | 12
        'notFound'       | null        | null   || true       | 0
        'init'           | 'init'      | 'init' || true       | 12
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
        when: '校验字段名字是否未被使用'
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
        name             | id   || expRequest | expResponse
        'init_name_text' | null || true       | false
        'init_name_text' | '10' || true       | true
        'name1'          | null || true       | true
    }

    def "listQuery"() {
        given: '准备工作'
        def url = baseUrl + "/fields?1=1"
        if (name != null) {
            url = url + "&name=" + name
        }
        if (description != null) {
            url = url + "&description=" + description
        }
        if (param != null) {
            url = url + "&param=" + param
        }
        when: '列表查询'
        ParameterizedTypeReference<List<FieldDTO>> typeRef = new ParameterizedTypeReference<List<FieldDTO>>() {
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
        name             | description | param  || expRequest | expResponseSize
        null             | null        | null   || true       | 12
        'init_name_text' | null        | null   || true       | 1
        null             | null        | 'init' || true       | 12
        'notFound'       | null        | null   || true       | 0
        'init'           | 'init'      | 'init' || true       | 12
    }

    def "queryById"() {
        given: '准备工作'
        def fieldId = id

        when: '根据id查询字段'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.GET, null, FieldDetailDTO, testOrginzationId, fieldId)

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
        null   || true       | false
    }

    def "update_related_page"() {
        given: '准备工作'
        PageDetailDTO page = new PageDetailDTO()
        page.setId(1L)
        page.setName("testPage")
        pageService.create(testOrginzationId, page);

        def url = baseUrl + "/update_related_page?1=1"
        if (fieldId != null) {
            url = url + "&field_id=" + fieldId
        }

        when: '更新关联页面'
        HttpEntity<List<Long>> httpEntity = new HttpEntity<>(Arrays.asList(pageId))
        ParameterizedTypeReference<List<Long>> typeRef = new ParameterizedTypeReference<List<Long>>() {
        };
        def entity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object, testOrginzationId)
        pageService.delete(testOrginzationId, 1L);

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody() instanceof ArrayList) {
                        actResponse = true
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        fieldId | pageId || expRequest | expResponse
        1       | 1      || true       | true
        1       | 9999   || true       | false
        9999    | 1      || true       | false
    }

    def "query_related_page"() {
        given: '准备工作'
        PageDetailDTO page = new PageDetailDTO()
        page.setId(1L)
        page.setName("testPage")
        pageService.create(testOrginzationId, page);
        fieldService.updateRelatedPage(testOrginzationId, 1L, Arrays.asList(1L))

        def url = baseUrl + "/query_related_page?1=1"
        if (fieldId != null) {
            url = url + "&field_id=" + fieldId
        }

        when: '获取关联页面'
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, Object, testOrginzationId)
        pageService.delete(testOrginzationId, 1L);

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponseSize = 0
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody() instanceof ArrayList) {
                        actResponseSize = entity.getBody().size();
                    }
                }
            }
        }
        actRequest == expRequest
        actResponseSize == expResponseSize

        where: '测试用例：'
        fieldId || expRequest | expResponseSize
        1       || true       | 1
        2       || true       | 0
    }

    def "checkDelete"() {
        given: '准备工作'
        def fieldOptionId = id
        def url = baseUrl2 + "/check_delete" + "/" + fieldOptionId + "?1=1"
        if (fieldId != null) {
            url = url + "&fieldId=" + fieldId
        }

        when: '校验问题类型是否可以删除'
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, Map, testOrginzationId)

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
        id   | fieldId || expRequest | expResponse
        1    | 1       || true       | true
        9999 | 1       || true       | false
        null | 1       || false      | false
    }
}
