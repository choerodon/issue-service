package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.PriorityDTO
import io.choerodon.issue.api.service.PriorityService
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
 *  测试编写说明：
 *  1、setup初始化数据执行在每一个单元测试的每一条测试数据之前
 *  2、调用controller方法失败（格式错误等），entity为空，actRequest为false
 *  3、调用成功，执行controller方法时抛出异常，未处理的异常，entity返回值非200，actRequest为false
 *  4、调用成功，执行controller方法时抛出异常，被捕获，entity返回值为200，actRequest为true
 *  5、在path和param中的参数均有null校验，若传null，调用成功，被spring抛出异常，被捕获，entity返回值为200，actRequest为true
 * @author cong.cheng
 * @date 2018/8/23
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class PriorityControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    PriorityService priorityService

    @Shared
    Long organizationId = 1L

    @Shared
    List<PriorityDTO> list = new ArrayList<>()

    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/priority'

    def "create"() {
        given: '准备工作'
        PriorityDTO priorityDTO = new PriorityDTO()
        priorityDTO.setName(name)
        priorityDTO.setDescription(description)
        priorityDTO.setColour(colour)
        priorityDTO.setDefault(isDefault)
        priorityDTO.setOrganizationId(organizationId)

        when: '创建优先级类型'
        HttpEntity<PriorityDTO> httpEntity = new HttpEntity<>(priorityDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, PriorityDTO, organizationId)

        then: '状态码为200，调用成功'
        def actRequest = false
        def actResponse = false
        if (entity != null) {
            if (entity.getStatusCode().is2xxSuccessful()) {
                actRequest = true
                if (entity.getBody() != null) {
                    if (entity.getBody().getId() != null) {
                        list.add(entity.body)
                        actResponse = true
                    }
                }
            }
        }
        actRequest == expRequest
        actResponse == expResponse

        where: '测试用例：'
        name    | isDefault | colour | description    || expRequest | expResponse
        'name1' | false     | 'aa'   | 'description1' || true       | true         //正常操作
        null    | false     | 'aa'   | 'description1' || true       | false         //name null
        'name2' | false     | null   | 'description1' || true       | false          //color null
        'name4' | null      | 'aa'   | 'description1' || true       | true       //isdefault null
//        'init_name1' | false     | 'aa'   | 'description1' || true       | false        //name 重复
        'name6' | true      | 'aa'   | 'description1' || true       | true         // 正常操作

    }

    def "update"() {
        given: '准备工作'
        PriorityDTO priorityDTO = list.get(0)
        priorityDTO.setName(name)
        priorityDTO.setDescription(description)
        priorityDTO.setColour(colour)
        priorityDTO.setDefault(isDefault)
        priorityDTO.setOrganizationId(organizationId)

        when: '更新优先级类型'
        HttpEntity<PriorityDTO> httpEntity = new HttpEntity<>(priorityDTO)
        def entity = restTemplate.exchange(baseUrl + '/{id}', HttpMethod.PUT, httpEntity, PriorityDTO, organizationId, priorityDTO.getId())

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
        name    | isDefault | colour | description    || expRequest | expResponse
        'name1' | false     | 'aa'   | 'description1' || true       | true        //正常操作
        'name2' | false     | null   | 'description1' || true       | false         //color null即不修改原有值
        'name3' | false     | 'aa'   | null           || true       | false        //描述可以为空 通过
        'name4' | null      | 'aa'   | 'description1' || true       | false      //isdefault null 默认值将会影响到其他字段的更新 不能为空
        'name6' | true      | 'aa'   | 'description1' || true       | false        // name 重复
    }

    def "selectAll"() {
        given: '准备工作'
        def url = baseUrl + "?1=1"
        if (name != null) {
            url = url + "&name=" + name
        }
        if (description != null) {
            url = url + "&description=" + description
        }
        if (colour != null) {
            url = url + "&colour=" + colour
        }
        if (param != null) {
            url = url + "&param=" + param
        }
        when: '展示优先级列表'
        ParameterizedTypeReference<List<PriorityDTO>> typeRef = new ParameterizedTypeReference<List<PriorityDTO>>() {
        }
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, typeRef, organizationId)

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
        name         | description         | colour         | param  || expRequest | expResponseSize
        null         | null                | null           | null   || true       | 6
        'init_name1' | null                | null           | null   || true       | 0
        null         | 'init_description1' | null           | null   || true       | 0
        null         | null                | null           | 'init' || true       | 0
        'notFound'   | null                | null           | null   || true       | 0
        'init'       | 'init'              | 'init'         | 'init' || true       | 0
        null         | null                | 'init_colour1' | null   || true       | 0
    }

    def "checkName"() {
        given: '准备工作'
        def url = baseUrl + "/check_name?1=1"
        if (name != null) {
            url = url + "&name=" + name
        }
        if (id != null) {
            url = url + "&priority_id=" + id
        }

        when: '校验优先级类型名字是否未被使用'
        def entity = restTemplate.exchange(url, HttpMethod.GET, null, Boolean.class, organizationId)

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
        'name6'      | null || true       | true
        'init_name1' | '1'  || true       | false
        'name1'      | null || true       | true
    }

    def "updateByList"() {
        given: '准备工作'
        def url = baseUrl + "/sequence"

        when: '更新展示顺序'
        List<PriorityDTO> reqList = new ArrayList<>()
        reqList.add(list.get(0))
        reqList.add(list.get(2))
        reqList.add(list.get(1))
        HttpEntity<List<PriorityDTO>> httpEntity = new HttpEntity(reqList)
        ParameterizedTypeReference<List<PriorityDTO>> typeRef = new ParameterizedTypeReference<List<PriorityDTO>>() {
        }
        def entity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, typeRef, organizationId)

        then: '状态码为200，调用成功'
        entity.getStatusCode().value() == 200
        List<PriorityDTO> e = entity.getBody()
        e.get(0).getId() == 1
        e.get(1).getId() == 2
        e.get(2).getId() == 4
        e.get(3).getId() == 3
        e.get(4).getId() == 6

    }

    def "queryByOrganizationId"() {
        given: '准备工作'
        def url = baseUrl + "/list"

        when: '更新展示顺序'
        def entity = restTemplate.getForEntity(url, Map, organizationId)

        then: '状态码为200，调用成功'
        entity.getStatusCode().is2xxSuccessful()
        Map<Long, PriorityDTO> priorityDTOMap = entity.body

        expect: "期望值"
        priorityDTOMap.get("1") != null
    }

    def "queryDefaultByOrganizationId"() {
        given: '准备工作'
        def url = baseUrl + "/default"

        when: '更新展示顺序'
        def entity = restTemplate.getForEntity(url, PriorityDTO, organizationId)

        then: '状态码为200，调用成功'
        entity.getStatusCode().is2xxSuccessful()
        PriorityDTO priorityDTO = entity.body

        expect: "期望值"
        priorityDTO != null
    }

    def "queryByOrganizationIdList"() {
        given: '准备工作'
        def url = baseUrl + "/list_by_org"

        when: '更新展示顺序'
        def entity = restTemplate.getForEntity(url, List, organizationId)

        then: '状态码为200，调用成功'
        entity.getStatusCode().is2xxSuccessful()
        List<PriorityDTO> priorityDTO = entity.body

        expect: "期望值"
        priorityDTO.size() == 6
    }

    def "queryById"() {
        given: '准备工作'
        def url = baseUrl + "/{id}"

        when: '更新展示顺序'
        def entity = restTemplate.getForEntity(url, PriorityDTO, organizationId, 1L)

        then: '状态码为200，调用成功'
        entity.getStatusCode().is2xxSuccessful()
        PriorityDTO priorityDTO = entity.body

        expect: "期望值"
        priorityDTO != null
    }

    def "delete"() {
        given: '准备工作'
        def priorityId = priority.id

        when: '删除优先级类型'
        def entity = restTemplate.exchange(baseUrl + "/{priority_id}", HttpMethod.DELETE, null, Object, organizationId, priorityId)

        then: '状态码为200，调用成功'
        entity.body

        where: '测试用例：'
        priority << list
    }
}
