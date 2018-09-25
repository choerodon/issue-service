package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.FieldDTO
import io.choerodon.issue.api.dto.PageDTO
import io.choerodon.issue.api.dto.PageDetailDTO
import io.choerodon.issue.api.service.FieldService
import io.choerodon.issue.api.service.PageFieldRefService
import io.choerodon.issue.api.service.PageService
import io.choerodon.issue.domain.Field
import io.choerodon.issue.domain.Page
import io.choerodon.issue.domain.PageFieldRef
import io.choerodon.issue.infra.enums.FieldType
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

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class PageControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    PageService pageService;

    @Autowired
    FieldService fieldService;

    @Autowired
    PageFieldRefService pageFieldRefService;

    @Shared
    Long orginzationId = 1L;

    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/page'

    @Shared
    List<PageDetailDTO> list = new ArrayList<>()

    @Shared
    List<FieldDTO> fieldList = new ArrayList<>()

    //初始化数据
    def setup() {
        //初始化field数据
        def fieldName = 'name-'
        def fieldDescription = 'description-'
        for (FieldType e : FieldType.values()) {
            FieldDTO fieldDTO = new FieldDTO();
            fieldDTO.setName(fieldName + e.value());
            fieldDTO.setDescription(fieldDescription + e.value());
            fieldDTO.setType(e.value())
            FieldDTO insertDTO = fieldService.create(orginzationId, fieldDTO)
            if (insertDTO != null) {
                fieldList.add(insertDTO)
            }
        }
        //初始化page数据
        def name = 'name'
        def description = 'description'
        for (int i = 1; i <= 40; i++) {
            PageDetailDTO pageDetailDTO = new PageDetailDTO();
            pageDetailDTO.setId(i);
            pageDetailDTO.setName(name + i)
            pageDetailDTO.setDescription(description + i)
            pageDetailDTO.setOrganizationId(orginzationId)
            FieldDTO fieldDTO2 = new FieldDTO();
            fieldDTO2.setId(fieldList.get(0).getId());
            pageDetailDTO.setFieldDTOs(Arrays.asList(fieldDTO2));
            PageDetailDTO insertDTO = pageService.create(orginzationId, pageDetailDTO)
            if (insertDTO != null) {
                list.add(insertDTO)
            }
        }
    }

    def cleanup() {
        //清空数据
        Field delField = new Field();
        fieldService.delete(delField);
        Page page = new Page();
        pageService.delete(page);
        PageFieldRef pageFieldRef = new PageFieldRef();
        pageFieldRefService.delete(pageFieldRef);
        fieldList.clear();
        list.clear();
    }

    def "create"() {
        given: '创建页面'
        PageDetailDTO pageDetailDTO = new PageDetailDTO();
        pageDetailDTO.setName(name)
        pageDetailDTO.setDescription(description)
        pageDetailDTO.setOrganizationId(orginzationId)
        FieldDTO fieldDTO = new FieldDTO();
        fieldDTO.setId(fieldList.get(1).getId());
        pageDetailDTO.setFieldDTOs(Arrays.asList(fieldDTO));

        when: '状态机方案写入数据库'
        HttpEntity<PageDetailDTO> httpEntity = new HttpEntity<>(pageDetailDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, PageDetailDTO, orginzationId)

        then: '状态码为200，创建成功'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        name         | description         || isSuccess | reponseResult
        'test-name1' | 'test-description1' || true      | true
        null         | 'test-description1' || true      | false
        null         | null                || true      | false
    }

    def "queryById"() {
        when: '根据id查询页面'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.GET, null, PageDetailDTO, orginzationId, pageId)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        pageId || isSuccess | reponseResult
        null   || true      | false
        1      || true      | true
        99     || true      | false
    }

    def "update"() {
        given: '修改页面'
        PageDetailDTO pageDetailDTO = new PageDetailDTO();
        pageDetailDTO.setName(name)
        pageDetailDTO.setDescription(description)
        pageDetailDTO.setObjectVersionNumber(1L)
        pageDetailDTO.setOrganizationId(orginzationId)

        when: '问题类型页面方案写入数据库'
        HttpEntity<PageDetailDTO> httpEntity = new HttpEntity<>(pageDetailDTO)
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.PUT, httpEntity, PageDetailDTO, orginzationId, pageId)

        then: '更新成功判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        (entity.getBody() != null && entity.getBody().getId() != null) == reponseResult

        where: '测试用例：'
        pageId | name         | description         || isSuccess | reponseResult
        1      | 'test-name1' | 'test-description1' || true      | true
//        list.get(0).getId() | null         | 'test-description1' || true      | false
//        list.get(0).getId() | null         | null                || true      | false
    }

    def "delete"() {
        when: '删除页面'
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.DELETE, null, Object, orginzationId, pageId)

        then: '删除结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        if (entity.getBody() != null && entity.getBody() instanceof Boolean) {
            entity.getBody() != null && entity.getBody() == reponseResult
        } else if (entity.getBody() != null) {
            Map map = (Map) entity.getBody();
            map.get("failed") == reponseResult;
            map.get("code") == "error.page.delete";
        }

        where: '测试用例：'
        pageId || isSuccess | reponseResult
//        null                || true      | false
        1      || true      | true
        99     || true      | false
    }


    def "pageQuery"() {
        when: '分页查询页面列表'
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
        ParameterizedTypeReference<io.choerodon.core.domain.Page<PageDTO>> typeRef = new ParameterizedTypeReference<io.choerodon.core.domain.Page<PageDTO>>() {
        };
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, orginzationId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        entity.getBody().size() == size

        where: '测试用例：'
        name     | description     | param  || isSuccess | size
        null     | null            | null   || true      | 20
        'name40' | null            | null   || true      | 1
        null     | 'description40' | null   || true      | 1
        'name40' | 'description40' | null   || true      | 1
        null     | null            | 'name' || true      | 20
    }


    def "checkName"() {
        when: '校验名字是否未被使用'
        def url = baseUrl + "/check_name?1=1"
        if (pageId != null) {
            url = url + "&id=" + pageId
        }
        if (name != null) {
            url = url + "&name=" + name
        }

        ParameterizedTypeReference<Boolean> typeRef = new ParameterizedTypeReference<Boolean>() {};
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, orginzationId)

        then: '结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        entity.getBody() != null && entity.getBody() == reponseResult

        where: '测试用例：'
        pageId | name       || isSuccess | reponseResult
//        null                | null       || false     | false
        null   | 'name1'    || true      | false
        null   | 'namename' || true      | true
        1      | 'name1'    || true      | true
        1      | 'name2'    || true      | false
        1      | 'testtest' || true      | true
//        list.get(0).getId() | null       || true      | false
    }


}
