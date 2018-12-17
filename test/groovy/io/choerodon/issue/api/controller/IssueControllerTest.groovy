//package io.choerodon.issue.api.controller
//
//import com.alibaba.fastjson.JSONObject
//import io.choerodon.core.domain.Page
//import io.choerodon.issue.IntegrationTestConfiguration
//import io.choerodon.issue.api.dto.IssueDTO
//import io.choerodon.issue.api.dto.IssueFieldValueDTO
//import io.choerodon.issue.api.dto.IssueTypeSchemeDTO
//import io.choerodon.issue.domain.IssueFieldValue
//import io.choerodon.issue.domain.ProjectConfig
//import io.choerodon.issue.infra.enums.SchemeType
//import io.choerodon.issue.infra.mapper.IssueFieldValueMapper
//import io.choerodon.issue.infra.mapper.IssueMapper
//import io.choerodon.issue.infra.mapper.ProjectConfigMapper
//import org.springframework.beans.BeanUtils
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.client.TestRestTemplate
//import org.springframework.context.annotation.Import
//import org.springframework.http.HttpEntity
//import org.springframework.http.HttpMethod
//import org.springframework.test.context.ActiveProfiles
//import spock.lang.Shared
//import spock.lang.Specification
//import spock.lang.Stepwise
//
///**
// *
// * @author dinghuang123@gmail.com
// * @since 2018/12/13
// */
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//@ActiveProfiles("test")
//@Stepwise
//class IssueControllerTest extends Specification {
//
//    @Autowired
//    TestRestTemplate restTemplate
//
//    @Autowired
//    IssueFieldValueMapper issueFieldValueMapper
//    @Autowired
//    IssueMapper issueMapper
//    @Autowired
//    ProjectConfigMapper projectConfigMapper
//
//    @Shared
//    Long projectId = 1L
//
//    @Shared
//    List<IssueDTO> issueDTOList = new ArrayList<>()
//    @Shared
//    List<IssueFieldValue> issueFieldValueArrayList = new ArrayList<>()
//
//
//    def "create"() {
//        given: '准备数据'
//        IssueDTO issueDTO = new IssueDTO()
//        issueDTO.projectId = projectId
//        issueDTO.description = "XX"
//        issueDTO.code = "XX"
//        issueDTO.statusId = 1L
//        issueDTO.priorityId = 1L
//        issueDTO.issueTypeId = 1L
//        issueDTO.subject = "XX"
//        ProjectConfig projectConfig = new ProjectConfig()
//        projectConfig.schemeId = 1L
//        projectConfig.projectId = 1L
//        projectConfig.applyType = "agile"
//        projectConfig.schemeType = SchemeType.FIELD_CONFIG
//        projectConfigMapper.insert(projectConfig)
//
//        when: '向创建问题，issueDTO中的fieldValues只传fieldId和fieldValue接口发请求'
//        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issue', issueDTO, IssueDTO, projectId)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//        and: '设置值'
//
//        IssueDTO result = entity.body
//        issueDTOList.add(result)
//
//        expect: '设置期望值'
//        result.projectId == projectId
//        result.code == "XX"
//        result.statusId == 1L
//        result.projectId == 1L
//        result.issueTypeId == 1L
//
//    }
//
//    def "queryByIssueTypeScheme"() {
//        when: '向加载问题类型列表接口发请求'
//        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue/query_issue_type_scheme', IssueTypeSchemeDTO, projectId)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//        and: '设置值'
//
//        IssueTypeSchemeDTO issueTypeSchemeDTO = entity.body
//
//        expect: '设置期望值'
//        issueTypeSchemeDTO.id != null
//    }
//
//    def "queryByIssueType"() {
//        when: '向创建问题时，根据问题类型id加载字段信息接口发请求'
//        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue/query_field/{issue_type_id}', List, projectId, 1L)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//        and: '设置值'
//
//        List<IssueFieldValueDTO> issueFieldValueDTOList = entity.body
//
//        expect: '设置期望值'
//        issueFieldValueDTOList.size() == 1
//    }
//
//    def "queryById"() {
//        when: '向根据id查询问题详情接口发请求'
//        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue/{id}', IssueDTO, projectId, issueDTOList.get(0).id)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//        and: '设置值'
//
//        IssueDTO issueDTO = entity.body
//
//        expect: '设置期望值'
//        issueDTO.projectId == projectId
//        issueDTO.code == "XX"
//        issueDTO.statusId == 1L
//        issueDTO.projectId == 1L
//        issueDTO.issueTypeId == 1L
//    }
//
//    def "update"() {
//        given: '准备数据'
//        JSONObject jsonObject = new JSONObject()
//        jsonObject.put("name", "XXX")
//        jsonObject.put("statusId", 2)
//        HttpEntity<JSONObject> requestEntity = new HttpEntity<JSONObject>(jsonObject, null)
//
//        when: '向修改问题接口发请求'
//        def entity = restTemplate.exchange('/v1/projects/{project_id}/issues/{id}', HttpMethod.PUT, requestEntity, IssueDTO, projectId, issueDTOList.get(0).id)
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//        and: '设置值'
//
//        IssueDTO issueDTO = entity.body
//
//        expect: '设置期望值'
//        issueDTO.projectId == projectId
//        issueDTO.code == "XXX"
//        issueDTO.statusId == 2L
//        issueDTO.projectId == 1L
//        issueDTO.issueTypeId == 1L
//    }
//
//    def "updateFieldValue"() {
//        given: '准备数据'
//        IssueFieldValue issueFieldValue = new IssueFieldValue()
//        issueFieldValue.fieldId = 1L
//        issueFieldValue.projectId = projectId
//        issueFieldValue.issueId = issueDTOList.get(0).id
//        issueFieldValue.fieldValue = "XX"
//        issueFieldValueMapper.insert(issueFieldValue)
//        issueFieldValueArrayList.add(issueFieldValue)
//        IssueFieldValueDTO issueFieldValueDTO = new IssueFieldValueDTO()
//        BeanUtils.copyProperties(issueFieldValue, issueFieldValueDTO)
//        issueFieldValueDTO.fieldValue = "XXX"
//        HttpEntity<IssueFieldValueDTO> requestEntity = new HttpEntity<IssueFieldValueDTO>(issueFieldValueDTO, null)
//
//        when: '向修改问题接口发请求'
//        def entity = restTemplate.exchange('/v1/projects/{project_id}/issues/update_field_value/{id}', HttpMethod.PUT, requestEntity, Long, projectId, issueDTOList.get(0).id)
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//        and: '设置值'
//
//        Long id = entity.body
//
//        expect: '设置期望值'
//        id == 1L
//    }
//
//    def "pageQuery"() {
//
//        when: '向分页查询问题列表接口发请求'
//        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issue/pageIssue?page={page}&&size={size}', Page, projectId, 1, 10000)
//
//        then: '返回值'
//        entity.statusCode.is2xxSuccessful()
//        and: '设置值'
//
//        Page<IssueDTO> issueDTOPage = entity.body
//
//        expect: '设置期望值'
//        issueDTOPage.content.size() == 1
//    }
//
//    def "delete"() {
//        given: '删除数据'
//        issueDTOList.each { issueDTO ->
//            issueMapper.deleteByPrimaryKey(issueDTO.id)
//        }
//        issueFieldValueArrayList.each { issueFieldValue ->
//            issueFieldValueMapper.deleteByPrimaryKey(issueFieldValue.id)
//        }
//    }
//}
