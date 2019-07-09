package io.choerodon.issue.api.config;


import com.github.pagehelper.PageInfo;
import io.choerodon.asgard.saga.dto.SagaInstanceDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.issue.api.dto.ExecuteResult;
import io.choerodon.issue.api.dto.ProjectDTO;
import io.choerodon.issue.api.dto.payload.StateMachineSchemeDeployCheckIssue;
import io.choerodon.issue.api.service.impl.SagaServiceImpl;
import io.choerodon.issue.infra.feign.AgileFeignClient;
import io.choerodon.issue.infra.feign.UserFeignClient;
import io.choerodon.issue.infra.feign.fallback.AgileFeignClientFallback;
import io.choerodon.issue.infra.feign.fallback.UserFeignClientFallback;
import io.choerodon.issue.infra.utils.ProjectUtil;
import io.choerodon.issue.statemachine.fegin.InstanceFeignClient;
import io.choerodon.issue.statemachine.fegin.InstanceFeignClientFallback;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/11/12
 */
@Configuration
public class FeignConfigure {
    @Bean
    @Primary
    UserFeignClient userFeignClient() {
        UserFeignClient userFeignClient = Mockito.mock(UserFeignClientFallback.class);
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(1L);
        projectDTO.setName("test");
        projectDTO.setOrganizationId(1L);
        Mockito.when(userFeignClient.queryProject(Matchers.anyLong())).thenReturn(new ResponseEntity<>(projectDTO, HttpStatus.OK));
        PageInfo<ProjectDTO> projectDTOS = new PageInfo<>();
        List<ProjectDTO> projectDTOList = new ArrayList<>(1);
        projectDTOList.add(projectDTO);
        projectDTOS.setList(projectDTOList);
        projectDTOS.setPageSize(1);
        projectDTOS.setSize(1);
        Mockito.when(userFeignClient.queryProjectsByOrgId(Matchers.anyLong(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(new ResponseEntity<>(projectDTOS, HttpStatus.OK));
        return userFeignClient;
    }

    @Bean
    @Primary
    InstanceFeignClient instanceFeignClient() {
        InstanceFeignClient instanceFeignClient = Mockito.mock(InstanceFeignClientFallback.class);
        ExecuteResult executeResult = new ExecuteResult();
        executeResult.setResultStatusId(1L);
        executeResult.setSuccess(true);
        Mockito.when(instanceFeignClient.startInstance(Matchers.anyLong(), Matchers.anyString(), Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(
                executeResult, HttpStatus.OK));
        Mockito.when(instanceFeignClient.executeTransform(Matchers.anyLong(), Matchers.anyString(), Matchers.anyLong(), Matchers.anyLong(), Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(executeResult, HttpStatus.OK));
        Mockito.when(instanceFeignClient.queryInitStatusId(Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(1L, HttpStatus.OK));
        return instanceFeignClient;
    }

    @Bean("projectUtil")
    @Primary
    ProjectUtil projectUtil() {
        ProjectUtil projectUtil = Mockito.mock(ProjectUtil.class);
        Mockito.when(projectUtil.getOrganizationId(Matchers.anyLong())).thenReturn(1L);
        Mockito.when(projectUtil.getCode(Matchers.anyLong())).thenReturn("test");
        Mockito.when(projectUtil.getName(Matchers.anyLong())).thenReturn("test");
        return projectUtil;
    }

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    AgileFeignClient agileFeignClient() {
        AgileFeignClient agileFeignClient = Mockito.mock(AgileFeignClientFallback.class);
        Map<String, Object> map = new HashMap<>();
        Map<Long, Long> map2 = new HashMap<>();
        map.put("XX", "XX");
        map2.put(1L, 1L);
        Mockito.when(agileFeignClient.checkDeleteNode(Matchers.anyLong(), Matchers.anyLong(), Matchers.any(List.class))).thenReturn(new ResponseEntity<>(map, HttpStatus.OK));
        Mockito.when(agileFeignClient.checkStateMachineSchemeChange(Matchers.anyLong(), Matchers.any(StateMachineSchemeDeployCheckIssue.class))).thenReturn(new ResponseEntity<>(map2, HttpStatus.OK));
        return agileFeignClient;
    }

    @Bean
    SagaClient sagaClient() {
        SagaClient sagaClient = Mockito.mock(SagaClient.class);
        Mockito.when(sagaClient.startSaga(Matchers.anyString(), Matchers.any(StartInstanceDTO.class))).thenReturn(new SagaInstanceDTO());
        SagaServiceImpl sagaService = ApplicationContextHelper.getSpringFactory().getBean(SagaServiceImpl.class);
        sagaService.setSagaClient(sagaClient);
        return sagaClient;
    }
}
