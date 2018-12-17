package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.FieldDTO
import io.choerodon.issue.api.dto.FieldOptionDTO
import io.choerodon.issue.api.service.FieldConfigService
import io.choerodon.issue.api.service.FieldService
import io.choerodon.issue.domain.FieldOption
import io.choerodon.issue.infra.enums.FieldType
import io.choerodon.issue.infra.mapper.FieldMapper
import io.choerodon.issue.infra.mapper.FieldOptionMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 *
 * @author dinghuang123@gmail.com
 * @since 2018/12/13
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class FieldOptionControllerTest extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    FieldService fieldService

    @Autowired
    FieldOptionMapper fieldOptionMapper

    @Autowired
    FieldMapper fieldMapper

    @Autowired
    FieldConfigService fieldConfigService

    @Shared
    Long organizationId = 1L

    @Shared
    List<FieldDTO> fieldDTOList = new ArrayList<>()
    @Shared
    List<FieldOption> fieldOptionList = new ArrayList<>()

    def "initData"() {
        given: '初始化数据'
        FieldDTO fieldInput = new FieldDTO()
        fieldInput.setId(1)
        fieldInput.setName("init_name_" + FieldType.INPUT.value())
        fieldInput.setDescription("init_description_" + FieldType.INPUT.value())
        fieldInput.setType(FieldType.INPUT.value())
        fieldInput.setDefaultValue("init_default_value")
        fieldInput.setExtraConfig(null)
        fieldInput.setOrganizationId(organizationId)
        fieldInput = fieldService.create(organizationId, fieldInput)
        FieldOption fieldOption = new FieldOption()
        fieldOption.fieldId = fieldInput.id
        fieldOption.parentId = 0
        fieldOption.value = "XX"
        fieldOptionMapper.insert(fieldOption)
        fieldDTOList.add(fieldInput)
        fieldOptionList.add(fieldOption)
    }

    def 'checkDelete'() {
        when: '向校验字段值是否可以删除的接口发请求'
        def entity = restTemplate.getForEntity('/v1/organizations/{organization_id}/field_option/check_delete/{id}?fieldId={fieldId}', Map.class, organizationId, fieldOptionList.get(0).id, fieldDTOList.get(0).id)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        Map<String, Object> map = entity.body

        expect: '设置期望值'
        map.get("canDelete") != null
        map.get("allChildIds") != null

    }

    def 'queryByFieldId'() {
        when: '向根据字段id查询字段选项列表的接口发请求'
        def entity = restTemplate.getForEntity('/v1/organizations/{organization_id}/field_option/{field_id}', List.class, organizationId, fieldDTOList.get(0).id)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        List<FieldOptionDTO> fieldOptionDTOList = entity.body

        expect: '设置期望值'
        fieldOptionDTOList.size() == 1

    }

    def 'deleteData'() {
        given: '清除数据'
        fieldOptionList.each { fieldOption ->
            fieldOptionMapper.delete(fieldOption)
        }
        fieldDTOList.each { fieldDTO ->
            fieldMapper.deleteByPrimaryKey(fieldDTO.id)
        }

    }
}
