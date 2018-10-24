package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.*
import io.choerodon.issue.api.service.*
import io.choerodon.issue.domain.*
import io.choerodon.issue.infra.enums.FieldType
import io.choerodon.issue.infra.enums.PageSchemeLineType
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class PageSchemeControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    PageService pageService;

    @Autowired
    FieldService fieldService;

    @Autowired
    PageFieldRefService pageFieldRefService;

    @Autowired
    PageSchemeService pageSchemeService;

    @Autowired
    PageSchemeLineService pageSchemeLineService;

    @Shared
    Long orginzationId = 1L;

    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/page_scheme'

    @Shared
    List<PageDetailDTO> pageList = new ArrayList<>()

    @Shared
    List<FieldDTO> fieldList = new ArrayList<>()

    @Shared
    List<PageSchemeDetailDTO> pageSchemeList = new ArrayList<>()

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
        def pageName = 'pageName'
        def pageDescription = 'pageDescription'
        for (int i = 1; i <= 10; i++) {
            PageDetailDTO pageDetailDTO = new PageDetailDTO();
            pageDetailDTO.setId(i);
            pageDetailDTO.setName(pageName + i)
            pageDetailDTO.setDescription(pageDescription + i)
            pageDetailDTO.setOrganizationId(orginzationId)
            FieldDTO fieldDTO2 = new FieldDTO();
            fieldDTO2.setId(fieldList.get(0).getId());
            pageDetailDTO.setFieldDTOs(Arrays.asList(fieldDTO2));
            PageDetailDTO insertDTO = pageService.create(orginzationId, pageDetailDTO)
            if (insertDTO != null) {
                pageList.add(insertDTO)
            }
        }

        //初始化pageScheme数据
        def name = 'name'
        def description = 'description'
        for (int i = 1; i <= 40; i++) {
            PageSchemeDetailDTO pageSchemeDetailDTO = new PageSchemeDetailDTO();
            pageSchemeDetailDTO.setId(i);
            pageSchemeDetailDTO.setName(name + i)
            pageSchemeDetailDTO.setDescription(description + i)
            pageSchemeDetailDTO.setOrganizationId(orginzationId)

            List<PageSchemeLineDTO> pageSchemeLineDTOS = new ArrayList<>();
            PageSchemeLineDTO lineDTO1 = new PageSchemeLineDTO();
            lineDTO1.setPageId(pageList.get(0).getId());
            lineDTO1.setType(PageSchemeLineType.DEFAULT.value());

            PageSchemeLineDTO lineDTO2 = new PageSchemeLineDTO();
            lineDTO2.setPageId(pageList.get(1).getId());
            lineDTO2.setType(PageSchemeLineType.CREATE.value());

            PageSchemeLineDTO lineDTO3 = new PageSchemeLineDTO();
            lineDTO3.setPageId(pageList.get(2).getId());
            lineDTO3.setType(PageSchemeLineType.EDIT.value());

            pageSchemeLineDTOS.add(lineDTO1);
            pageSchemeLineDTOS.add(lineDTO2);
            pageSchemeLineDTOS.add(lineDTO3);
            pageSchemeDetailDTO.setPageSchemeLineDTOS(pageSchemeLineDTOS);
            PageSchemeDetailDTO insertDTO = pageSchemeService.create(orginzationId, pageSchemeDetailDTO)
            if (insertDTO != null) {
                pageSchemeList.add(insertDTO)
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
        PageScheme pageScheme = new PageScheme();
        pageSchemeService.delete(pageScheme);
        PageSchemeLine pageSchemeLine = new PageSchemeLine();
        pageSchemeLineService.delete(pageSchemeLine);
        fieldList.clear();
        pageList.clear();
        pageSchemeList.clear();
    }

    def "create"() {
        given: '创建页面'
        PageSchemeDetailDTO pageSchemeDetailDTO = new PageSchemeDetailDTO();
        pageSchemeDetailDTO.setName(name)
        pageSchemeDetailDTO.setDescription(description)
        pageSchemeDetailDTO.setOrganizationId(orginzationId)

        List<PageSchemeLineDTO> pageSchemeLineDTOS = new ArrayList<>();
        PageSchemeLineDTO lineDTO1 = new PageSchemeLineDTO();
        lineDTO1.setPageId(pageList.get(0).getId());
        lineDTO1.setType(PageSchemeLineType.DEFAULT.value());

        PageSchemeLineDTO lineDTO2 = new PageSchemeLineDTO();
        lineDTO2.setPageId(pageList.get(1).getId());
        lineDTO2.setType(PageSchemeLineType.CREATE.value());

        PageSchemeLineDTO lineDTO3 = new PageSchemeLineDTO();
        lineDTO3.setPageId(pageList.get(2).getId());
        lineDTO3.setType(PageSchemeLineType.EDIT.value());

        pageSchemeLineDTOS.add(lineDTO1);
        pageSchemeLineDTOS.add(lineDTO2);
        pageSchemeLineDTOS.add(lineDTO3);
        pageSchemeDetailDTO.setPageSchemeLineDTOS(pageSchemeLineDTOS);

        when: '状态机方案写入数据库'
        HttpEntity<PageSchemeDetailDTO> httpEntity = new HttpEntity<>(pageSchemeDetailDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, PageSchemeDetailDTO, orginzationId)

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
        PageSchemeDetailDTO pageSchemeDetailDTO = new PageSchemeDetailDTO();
        pageSchemeDetailDTO.setName(name)
        pageSchemeDetailDTO.setDescription(description)
        pageSchemeDetailDTO.setOrganizationId(orginzationId)
        pageSchemeDetailDTO.setObjectVersionNumber(1L)

        List<PageSchemeLineDTO> pageSchemeLineDTOS = new ArrayList<>();
        PageSchemeLineDTO lineDTO1 = new PageSchemeLineDTO();
        lineDTO1.setPageId(pageList.get(0).getId());
        lineDTO1.setType(PageSchemeLineType.DEFAULT.value());

        PageSchemeLineDTO lineDTO2 = new PageSchemeLineDTO();
        lineDTO2.setPageId(pageList.get(1).getId());
        lineDTO2.setType(PageSchemeLineType.CREATE.value());

        PageSchemeLineDTO lineDTO3 = new PageSchemeLineDTO();
        lineDTO3.setPageId(pageList.get(2).getId());
        lineDTO3.setType(PageSchemeLineType.EDIT.value());

        pageSchemeLineDTOS.add(lineDTO1);
        pageSchemeLineDTOS.add(lineDTO2);
        pageSchemeLineDTOS.add(lineDTO3);
        pageSchemeDetailDTO.setPageSchemeLineDTOS(pageSchemeLineDTOS);

        when: '问题类型页面方案写入数据库'
        HttpEntity<PageSchemeDetailDTO> httpEntity = new HttpEntity<>(pageSchemeDetailDTO)
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.PUT, httpEntity, PageSchemeDetailDTO, orginzationId, pageId)

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
        def entity = restTemplate.exchange(baseUrl + "/{id}", HttpMethod.DELETE, null, Object, orginzationId, pageSchemeId)

        then: '删除结果判断'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        if (entity.getBody() != null && entity.getBody() instanceof Boolean) {
            entity.getBody() != null && entity.getBody() == reponseResult
        } else if (entity.getBody() != null) {
            Map map = (Map) entity.getBody();
            map.get("failed") == reponseResult;
            map.get("code") == "error.pageScheme.delete";
        }

        where: '测试用例：'
        pageSchemeId || isSuccess | reponseResult
//        null                || true      | false
        10           || true      | true
        99           || true      | false
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
        ParameterizedTypeReference<io.choerodon.core.domain.Page<PageSchemeDTO>> typeRef = new ParameterizedTypeReference<io.choerodon.core.domain.Page<PageSchemeDTO>>() {
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


    def "queryAll"() {
        when: '分页查询页面列表'
        def url = baseUrl + "/query_all"
        ParameterizedTypeReference<List<PageSchemeDetailDTO>> typeRef = new ParameterizedTypeReference<List<PageSchemeDetailDTO>>() {
        };
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, orginzationId)

        then: '返回结果'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        entity.getBody().size() == size

        where: '测试用例：'
        isSuccess | size
        true      | 40
    }


}
