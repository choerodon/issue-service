package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.ProjectConfigDetailDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
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
class ProjectConfigControllerTest extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    def "queryById"() {
        when: '获取项目配置方案信息'
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/project_configs", ProjectConfigDetailDTO, 1L)

        then: '状态码为200，调用成功'
        entity.getStatusCode().is2xxSuccessful()
        ProjectConfigDetailDTO projectConfigDetailDTO = entity.body

        expect: "期望值"
        projectConfigDetailDTO != null
    }
}
