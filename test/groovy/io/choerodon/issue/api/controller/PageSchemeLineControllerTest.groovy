package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.PageDetailDTO
import io.choerodon.issue.api.dto.PageSchemeDetailDTO
import io.choerodon.issue.api.dto.PageSchemeLineDTO
import io.choerodon.issue.api.service.PageSchemeService
import io.choerodon.issue.api.service.PageService
import io.choerodon.issue.infra.mapper.PageMapper
import io.choerodon.issue.infra.mapper.PageSchemeLineMapper
import io.choerodon.issue.infra.mapper.PageSchemeMapper
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
 *
 * @author dinghuang123@gmail.com
 * @since 2018/12/13
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class PageSchemeLineControllerTest extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    Long organizationId = 1L

    @Shared
    List<PageDetailDTO> pageDetailDTOS = new ArrayList<>()

    @Autowired
    PageMapper pageMapper

    @Autowired
    PageSchemeMapper pageSchemeMapper

    @Autowired
    PageSchemeLineMapper pageSchemeLineMapper

    @Shared
    List<PageSchemeDetailDTO> pageSchemeDetailDTOS = new ArrayList<>()

    @Shared
    List<PageSchemeLineDTO> pageSchemeLineDTOArrayList = new ArrayList<>()

    @Autowired
    PageService pageService

    @Autowired
    PageSchemeService pageSchemeService

    @Shared
    Long pageId

    @Shared
    Long schemeId


    def "create"() {
        given: '准备数据'
        PageDetailDTO pageDetailDTO = new PageDetailDTO()
        pageDetailDTO.setName("XX")
        pageDetailDTO.setDescription("XX")
        pageDetailDTO.setOrganizationId(organizationId)
        PageDetailDTO insertDTO = pageService.create(organizationId, pageDetailDTO)
        pageDetailDTOS.add(insertDTO)
        PageSchemeDetailDTO pageSchemeDetailDTO = new PageSchemeDetailDTO()
        pageSchemeDetailDTO.setName("XX")
        pageSchemeDetailDTO.setDescription("XX")
        pageSchemeDetailDTO.setOrganizationId(organizationId)
        PageSchemeLineDTO pageSchemeLineDTO1 = new PageSchemeLineDTO()
        pageSchemeLineDTO1.pageId = insertDTO.id
        pageId = insertDTO.id
        pageSchemeLineDTO1.organizationId = organizationId
        pageSchemeLineDTO1.pageName = "XX"
        pageSchemeLineDTO1.type = "default"
        List<PageSchemeLineDTO> pageSchemeLineDTOList = new ArrayList<>(1)
        pageSchemeLineDTOList.add(pageSchemeLineDTO1)
        pageSchemeDetailDTO.setPageSchemeLineDTOS(pageSchemeLineDTOList)
        PageSchemeDetailDTO pageSchemeDetailDTO1 = pageSchemeService.create(organizationId, pageSchemeDetailDTO)
        pageSchemeDetailDTOS.add(pageSchemeDetailDTO1)
        pageSchemeLineDTO1.setId(pageSchemeLineMapper.selectAll().get(0).id)
        pageSchemeLineDTOArrayList.add(pageSchemeLineDTO1)
        PageSchemeLineDTO pageSchemeLineDTO = new PageSchemeLineDTO()
        pageSchemeLineDTO.pageId = insertDTO.id
        pageSchemeLineDTO.schemeId = pageSchemeDetailDTO1.id
        schemeId = pageSchemeDetailDTO1.id
        pageSchemeLineDTO.organizationId = organizationId
        pageSchemeLineDTO.pageName = "XX"
        pageSchemeLineDTO.type = "create"
        HttpEntity<PageSchemeLineDTO> pageSchemeLineDTOHttpEntity = new HttpEntity<>(pageSchemeLineDTO)
        when: '创建页面方案配置'
        def entity = restTemplate.exchange("/v1/organizations/{organization_id}/page_scheme_line", HttpMethod.POST, pageSchemeLineDTOHttpEntity, PageSchemeLineDTO, organizationId)

        then: '状态码为200，创建成功'
        entity.getStatusCode().is2xxSuccessful()
        pageSchemeLineDTOArrayList.add(entity.body)
        expect: '验证条件'
        entity.body.type == "create"
        entity.body.pageId == insertDTO.id
        entity.body.schemeId == pageSchemeDetailDTO1.id
    }

    def "queryById"() {
        when: '根据id查询页面方案配置'
        def entity = restTemplate.getForEntity("/v1/organizations/{organization_id}/page_scheme_line/{id}", PageSchemeLineDTO, organizationId, pageSchemeLineDTOArrayList.get(1).id)

        then: '状态码为200，创建成功'
        entity.getStatusCode().is2xxSuccessful()
        PageSchemeLineDTO pageSchemeLineDTO = entity.body
        expect: '验证条件'
        pageSchemeLineDTO.type == "create"
    }

    def "update"() {
        given: "准备数据"
        PageSchemeLineDTO pageSchemeLineDTO1 = new PageSchemeLineDTO()
        pageSchemeLineDTO1.pageName = "XXS"
        pageSchemeLineDTO1.type = "edit"
        pageSchemeLineDTO1.pageId = pageId
        pageSchemeLineDTO1.schemeId = schemeId
        pageSchemeLineDTO1.objectVersionNumber = 1L
        HttpEntity<PageSchemeLineDTO> pageSchemeLineDTOHttpEntity = new HttpEntity<>(pageSchemeLineDTO1)

        when: '修改页面方案配置'
        def entity = restTemplate.exchange("/v1/organizations/{organization_id}/page_scheme_line/{id}", HttpMethod.PUT, pageSchemeLineDTOHttpEntity, PageSchemeLineDTO, organizationId, pageSchemeLineDTOArrayList.get(1).id)

        then: '状态码为200，创建成功'
        entity.getStatusCode().is2xxSuccessful()
        PageSchemeLineDTO pageSchemeLineDTO = entity.body

        expect: '验证条件'
        pageSchemeLineDTO.type == "edit"
    }

    def "checkDelete"() {

        when: '校验页面方案配置是否可以删除'
        def entity = restTemplate.getForEntity("/v1/organizations/{organization_id}/page_scheme_line/check_delete/{id}", Map, organizationId, pageSchemeLineDTOArrayList.get(1).id)

        then: '状态码为200，创建成功'
        entity.getStatusCode().is2xxSuccessful()
        Map<String, Object> map = entity.body

        expect: '验证条件'
        map.get("canDelete")
    }

    def "delete"() {
        when: '校验页面方案配置是否可以删除'
        def entity = restTemplate.exchange("/v1/organizations/{organization_id}/page_scheme_line/{id}", HttpMethod.DELETE, null, Object, 1, 1)

        then: '状态码为200，创建成功'
        entity.getStatusCode().is2xxSuccessful()
        pageSchemeLineDTOArrayList.remove(1)

        expect: '验证条件'
        entity.body
    }

    def "deleteData"() {
        given: "删除数据"
        pageDetailDTOS.each { pageDetailDTOS ->
            pageMapper.deleteByPrimaryKey(pageDetailDTOS.id)
        }
        pageSchemeDetailDTOS.each { pageSchemeDetailDTOS ->
            pageSchemeMapper.deleteByPrimaryKey(pageSchemeDetailDTOS.id)
        }
        pageSchemeLineDTOArrayList.each { pageSchemeLineDTO ->
            pageSchemeLineMapper.deleteByPrimaryKey(pageSchemeLineDTO.id)
        }
    }
}
