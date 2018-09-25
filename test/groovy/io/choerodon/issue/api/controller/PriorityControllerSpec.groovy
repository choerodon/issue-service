package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.PriorityDTO
import io.choerodon.issue.api.service.PriorityService
import io.choerodon.issue.domain.Priority
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
 * @author cong.cheng
 * @date 2018/8/23
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class PriorityControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    PriorityService priorityService;

    @Shared
    Long testOrginzationId = 1L;

    @Shared
    List<PriorityDTO> list = new ArrayList<>();

    @Shared
    String baseUrl = '/v1/organizations/{organization_id}/priority'

    /**
     * 测试编写说明：
     * 1、setup初始化数据执行在每一个单元测试的每一条测试数据之前
     * 2、调用controller方法失败（格式错误等），entity为空，actRequest为false
     * 3、调用成功，执行controller方法时抛出异常，未处理的异常，entity返回值非200，actRequest为false
     * 4、调用成功，执行controller方法时抛出异常，被捕获，entity返回值为200，actRequest为true
     * 5、在path和param中的参数均有null校验，若传null，调用成功，被spring抛出异常，被捕获，entity返回值为200，actRequest为true
     */

    //初始化5条数据
    def setup() {
        println "执行初始化"
        for (int i = 1; i <= 5; i++) {
            PriorityDTO priorityDTO = new PriorityDTO()
            priorityDTO.setId(i)
            priorityDTO.setName("init_name" + i)
            priorityDTO.setColour("init_colour" + i)
            priorityDTO.setDescription("init_description" + i)
            priorityDTO.setOrganizationId(testOrginzationId)
            priorityDTO.setIsDefault("1")
            priorityDTO.setSequence(new BigDecimal(i))
            priorityDTO = priorityService.create(testOrginzationId, priorityDTO)
            list.add(priorityDTO)
        }
    }

    def cleanup(){
        Priority del = new Priority()
        priorityService.delete(del);//清空数据
        list.clear();
    }

    def "create"() {
        given: '准备工作'
        PriorityDTO priorityDTO = new PriorityDTO();
        priorityDTO.setName(name);
        priorityDTO.setDescription(description);
        priorityDTO.setColour(colour);
        priorityDTO.setIsDefault(isDefault)
        priorityDTO.setOrganizationId(testOrginzationId);

        when: '创建优先级类型'
        HttpEntity<PriorityDTO> httpEntity = new HttpEntity<>(priorityDTO)
        def entity = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, PriorityDTO, testOrginzationId)

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
        name         | isDefault | colour         | description    || expRequest | expResponse
        'name1'      | '0'       | 'aa'           | 'description1' || true       | true         //正常操作
        null         | '0'       | 'aa'           | 'description1' || true       | false         //name null
        'name2'      | '0'       | null           | 'description1' || true       | false          //color null
        'name3'      | '0'       | 'aa'           | null           || true       | true           //描述可以为空 通过
        'name4'      | null      | 'aa'           | 'description1' || false      | false       //isdefault null
        'init_name1' | '0'       | 'aa'           | 'description1' || true       | false        //name 重复
        'name6'      | '1'       | 'aa'           | 'description1' || true       | true         // 正常操作

    }

    def "update"() {
        given: '准备工作'
        PriorityDTO priorityDTO = list.get(0);
        priorityDTO.setName(name);
        priorityDTO.setDescription(description);
        priorityDTO.setColour(colour);
        priorityDTO.setIsDefault(isDefault)
        priorityDTO.setOrganizationId(testOrginzationId);

        when: '更新优先级类型'
        HttpEntity<PriorityDTO> httpEntity = new HttpEntity<>(priorityDTO)
        def entity = restTemplate.exchange(baseUrl + '/{id}', HttpMethod.PUT, httpEntity, PriorityDTO, testOrginzationId, priorityDTO.getId())

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
        name         | isDefault | colour         | description    || expRequest | expResponse
        'name1'      | '0'       | 'aa'           | 'description1' || true       | true        //正常操作
        null         | '0'       | 'aa'           | 'description1' || true       | true        //name null即不修改原有值 通过
        'name2'      | '0'       | null           | 'description1' || true       | true        //color null即不修改原有值 通过
        'name3'      | '0'       | 'aa'           | null           || true       | true        //描述可以为空 通过
        'name4'      | null      | 'aa'           | 'description1' || false      | false      //isdefault null 默认值将会影响到其他字段的更新 不能为空
        'init_name2' | '0'       | 'aa'           | 'description1' || true       | false       //name 重复
        'name6'      | '1'       | 'aa'           | 'description1' || true       | true        // 正常操作
    }


    def "delete"() {
        given: '准备工作'
        def priorityId = id

        when: '删除优先级类型'
        def entity = restTemplate.exchange(baseUrl + "/{priority_id}", HttpMethod.DELETE, null, Object, testOrginzationId, priorityId)

        then: '状态码为200，调用成功'
        entity.getStatusCode().is2xxSuccessful() == isSuccess
        if (entity.getBody() != null && entity.getBody() instanceof Boolean) {
            entity.getBody() != null && entity.getBody() == reponseResult
        } else if (entity.getBody() != null) {
            Map map = (Map) entity.getBody();
            map.get("failed") == reponseResult;
            map.get("code") == "error.priority.delete";
        }

        where: '测试用例：'
        id   || isSuccess | reponseResult
        '1'  || true      | true     //删除成功
        '10' || true      | false    //抛出异常,删除失败
        // null || false     | false    //抛出异常
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
        name         | description         | colour         | param  || expRequest | expResponseSize
        null         | null                | null           | null   || true       | 5
        'init_name1' | null                | null           | null   || true       | 1
        null         | 'init_description1' | null           | null   || true       | 1
        null         | null                | null           | 'init' || true       | 5
        'notFound'   | null                | null           | null   || true       | 0
        'init'       | 'init'              | 'init'         | 'init' || true       | 5
        null         | null                | 'init_colour1' | null   || true       | 1
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
//        then:
//        thrown(Exception)

        where: '测试用例：'
        name         | id   || expRequest | expResponse
        'init_name1' | null || true       | false
        'init_name1' | '1'  || true       | true
        'name1'      | null || true       | true
    }

    def "sequence"() {
        given: '准备工作'
        def url = baseUrl + "/sequence"

        when: '拖拽排序'
        List<PriorityDTO> reqList = new ArrayList<>();
        reqList.add(list.get(0));
        reqList.add(list.get(2));
        reqList.add(list.get(1));
        reqList.add(list.get(3));
        reqList.add(list.get(4));
        HttpEntity<List<PriorityDTO>> httpEntity = new HttpEntity(reqList);
        ParameterizedTypeReference<List<PriorityDTO>> typeRef = new ParameterizedTypeReference<List<PriorityDTO>>() {
        };
        def entity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, typeRef, testOrginzationId)

        then: '状态码为200，调用成功'
        entity.getStatusCode().value() == 200
        List<PriorityDTO> e = entity.getBody()
        e.get(0).getId() == 1
        e.get(1).getId() == 3
        e.get(2).getId() == 2
        e.get(3).getId() == 4
        e.get(4).getId() == 5

    }
}
