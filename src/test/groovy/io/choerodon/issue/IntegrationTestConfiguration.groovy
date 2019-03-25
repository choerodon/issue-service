package io.choerodon.issue

import com.alibaba.fastjson.JSON
import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.issue.api.dto.payload.OrganizationCreateEventPayload
import io.choerodon.issue.api.dto.payload.ProjectEvent
import io.choerodon.issue.api.eventhandler.IssueEventHandler
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.issue.infra.enums.ProjectCategory
import io.choerodon.issue.infra.mapper.ProjectConfigMapper
import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.Order
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.MacSigner
import org.springframework.security.jwt.crypto.sign.Signer
import spock.mock.DetachedMockFactory

import javax.annotation.PostConstruct

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by hailuoliu@choerodon.io on 2018/7/13.
 */
@TestConfiguration
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(LiquibaseConfig)
class IntegrationTestConfiguration {

    private final detachedMockFactory = new DetachedMockFactory()

    @Value('${choerodon.oauth.jwt.key:choerodon}')
    String key

    @Autowired
    TestRestTemplate testRestTemplate
    @Autowired
    IssueEventHandler issueEventHandler
    @Autowired
    ProjectConfigMapper projectConfigMapper

    @Autowired
    LiquibaseExecutor liquibaseExecutor

    final ObjectMapper objectMapper = new ObjectMapper()

    @PostConstruct
    void init() {
        liquibaseExecutor.execute()
        setTestRestTemplateJWT()
        OrganizationCreateEventPayload organizationEvent = new OrganizationCreateEventPayload()
        organizationEvent.id = 1
        issueEventHandler.handleOrgaizationCreateByConsumeSagaTask(JSON.toJSONString(organizationEvent))
        ProjectEvent projectEvent = new ProjectEvent()
        projectEvent.projectId = 1
        projectEvent.projectCode = "test"
        projectEvent.projectCategory = ProjectCategory.AGILE
        issueEventHandler.handleProjectInitByConsumeSagaTask(JSON.toJSONString(projectEvent))
        System.out.print("初始化数据成功")
    }

    private void setTestRestTemplateJWT() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory())
        testRestTemplate.getRestTemplate().setInterceptors([new ClientHttpRequestInterceptor() {
            @Override
            ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                httpRequest.getHeaders()
                        .add('JWT_Token', createJWT(key, objectMapper))
                return clientHttpRequestExecution.execute(httpRequest, bytes)
            }
        }])
    }

    static String createJWT(final String key, final ObjectMapper objectMapper) {
        Signer signer = new MacSigner(key)
        CustomUserDetails defaultUserDetails = new CustomUserDetails('default', 'unknown', Collections.emptyList())
        defaultUserDetails.setUserId(1L)
        defaultUserDetails.setOrganizationId(1L)
        defaultUserDetails.setLanguage('zh_CN')
        defaultUserDetails.setTimeZone('CCT')
        String jwtToken = null
        try {
            jwtToken = 'Bearer ' + JwtHelper.encode(objectMapper.writeValueAsString(defaultUserDetails), signer).getEncoded()
        } catch (IOException e) {
            e.printStackTrace()
        }
        return jwtToken
    }

    @TestConfiguration
    @Order(1)
    static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().ignoringAntMatchers("/h2-console/**")
                    .and()
        }
    }
}
