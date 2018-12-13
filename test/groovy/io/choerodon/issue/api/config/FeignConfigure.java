package io.choerodon.issue.api.config;


import io.choerodon.asgard.saga.dto.SagaInstanceDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.asgard.saga.feign.SagaClientCallback;
import io.choerodon.core.domain.Page;
import io.choerodon.issue.api.dto.ProjectDTO;
import io.choerodon.issue.api.dto.UserDTO;
import io.choerodon.issue.api.dto.payload.ProjectEvent;
import io.choerodon.issue.api.dto.payload.StateMachineSchemeDeployCheckIssue;
import io.choerodon.issue.infra.feign.AgileFeignClient;
import io.choerodon.issue.infra.feign.FileFeignClient;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.UserFeignClient;
import io.choerodon.issue.infra.feign.dto.*;
import io.choerodon.issue.infra.feign.fallback.AgileFeignClientFallback;
import io.choerodon.issue.infra.feign.fallback.FileFeignClientFallback;
import io.choerodon.issue.infra.feign.fallback.StateMachineFeignClientFallback;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;

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
        UserDTO userDTO = new UserDTO();
        userDTO.setAdmin(false);
        userDTO.setEmail("XX");
        userDTO.setEnabled(true);
        userDTO.setId(1L);
        userDTO.setImageUrl("XX");
        userDTO.setLanguage("XX");
        userDTO.setLdap(true);
        userDTO.setObjectVersionNumber(1L);
        userDTO.setRealName("XX");
        userDTO.setLoginName("XX");
        Mockito.when(userFeignClient.queryInfo(Matchers.anyLong())).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        Page<ProjectDTO> projectDTOS = new Page<>();
        List<ProjectDTO> projectDTOList = new ArrayList<>(1);
        projectDTOList.add(projectDTO);
        projectDTOS.setContent(projectDTOList);
        projectDTOS.setTotalPages(1);
        projectDTOS.setSize(1);
        Mockito.when(userFeignClient.queryProjectsByOrgId(Matchers.anyLong(), Matchers.anyInt(), Matchers.anyInt(), Matchers.any(String[].class), Matchers.anyString(), Matchers.anyString(), Matchers.anyBoolean(), Matchers.any(String[].class))).thenReturn(new ResponseEntity<>(projectDTOS, HttpStatus.OK));
        return userFeignClient;
    }

    @Bean
    @Primary
    FileFeignClient fileFeignClient() {
        FileFeignClient fileFeignClient = Mockito.mock(FileFeignClientFallback.class);
        Mockito.when(fileFeignClient.uploadFile(anyString(), anyString(), any(MultipartFile.class))).thenReturn(new ResponseEntity<>(
                "https://minio.choerodon.com.cn/agile-service/file_56a005f56a584047b538d5bf84b17d70_blob.png", HttpStatus.OK));
        Mockito.when(fileFeignClient.deleteFile(Matchers.anyString(), Matchers.anyString())).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        return fileFeignClient;
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
    @Primary
    SagaClient sagaClient() {

        SagaClient sagaClient = Mockito.mock(SagaClientCallback.class);
        Mockito.when(sagaClient.startSaga(Matchers.anyString(), Matchers.any(StartInstanceDTO.class))).thenReturn(new SagaInstanceDTO());
        return sagaClient;
    }

    @Bean
    @Primary
    StateMachineFeignClient stateMachineFeignClient() {
        StateMachineFeignClient stateMachineFeignClient = Mockito.mock(StateMachineFeignClientFallback.class);
        StateMachineDTO stateMachineDTO = new StateMachineDTO();
        stateMachineDTO.setDefault(true);
        stateMachineDTO.setDescription("XX");
        stateMachineDTO.setName("XX");
        stateMachineDTO.setOrganizationId(1L);
        stateMachineDTO.setStatus("todo");
        stateMachineDTO.setId(1L);
        Mockito.when(stateMachineFeignClient.queryStateMachineById(Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(stateMachineDTO, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.queryDefaultStateMachine(Matchers.anyLong())).thenReturn(new ResponseEntity<>(stateMachineDTO, HttpStatus.OK));
        Page<StateMachineDTO> page = new Page<>();
        page.setNumber(1);
        page.setSize(1);
        page.setTotalPages(1);
        List<StateMachineDTO> stateMachineDTOS = new ArrayList<>(1);
        stateMachineDTOS.add(stateMachineDTO);
        page.setContent(stateMachineDTOS);
        Mockito.when(stateMachineFeignClient.pagingQuery(Matchers.anyLong(), Matchers.anyInt(), Matchers.anyInt(), Matchers.any(String[].class),
                Matchers.anyString(), Matchers.anyString(), Matchers.any(String[].class))).thenReturn(new ResponseEntity<>(page, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.delete(Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));
        StatusDTO statusDTO = new StatusDTO();
        statusDTO.setId(1L);
        statusDTO.setOrganizationId(1L);
        statusDTO.setObjectVersionNumber(1L);
        statusDTO.setCanDelete(true);
        statusDTO.setCode("todo");
        statusDTO.setDescription("todo");
        statusDTO.setType("todo");
        Mockito.when(stateMachineFeignClient.queryStatusById(Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(statusDTO, HttpStatus.OK));
        List<StatusDTO> statusDTOS = new ArrayList<>(1);
        statusDTOS.add(statusDTO);
        Mockito.when(stateMachineFeignClient.queryAllStatus(Matchers.anyLong())).thenReturn(new ResponseEntity<>(statusDTOS, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.createStateMachineWithCreateProject(Matchers.anyLong(), Matchers.anyString(), Matchers.any(ProjectEvent.class))).thenReturn(new ResponseEntity<>(1L, HttpStatus.OK));
        List<TransformDTO> transformDTOS = new ArrayList<>(1);
        TransformDTO transformDTO = new TransformDTO();
        transformDTO.setId(1L);
        transformDTO.setStatusDTO(statusDTO);
        transformDTO.setEndStatusId(2L);
        transformDTO.setName("XXX");
        transformDTO.setStateMachineId(1L);
        Mockito.when(stateMachineFeignClient.transformList(Matchers.anyLong(), Matchers.anyString(), Matchers.anyLong(), Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(transformDTOS, HttpStatus.OK));
        StatusDTO statusDTO1 = new StatusDTO();
        statusDTO1.setId(2L);
        statusDTO1.setType("XXX");
        statusDTO1.setDescription("XXX");
        statusDTO1.setCode("XXX");
        statusDTO1.setCanDelete(true);
        statusDTO1.setName("XXX");
        statusDTO1.setObjectVersionNumber(1L);
        statusDTO1.setOrganizationId(1L);
        Mockito.when(stateMachineFeignClient.queryByStateMachineIds(Matchers.anyLong(), Matchers.anyListOf(Long.class))).thenReturn(new ResponseEntity<>(statusDTOS, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.activeStateMachines(Matchers.anyLong(), Matchers.anyListOf(Long.class))).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.notActiveStateMachines(Matchers.anyLong(), Matchers.anyListOf(Long.class))).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));
        List<StateMachineWithStatusDTO> stateMachineWithStatusDTOS = new ArrayList<>(1);
        StateMachineWithStatusDTO stateMachineWithStatusDTO = new StateMachineWithStatusDTO();
        stateMachineWithStatusDTO.setDescription("XXXX");
        stateMachineWithStatusDTO.setId(1L);
        stateMachineWithStatusDTO.setName("XXX");
        stateMachineWithStatusDTO.setOrganizationId(1L);
        stateMachineWithStatusDTO.setStatus("XXX");
        stateMachineWithStatusDTO.setStatusDTOS(statusDTOS);
        stateMachineWithStatusDTOS.add(stateMachineWithStatusDTO);
        Mockito.when(stateMachineFeignClient.queryAllWithStatus(Matchers.anyLong())).thenReturn(new ResponseEntity<>(stateMachineWithStatusDTOS, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.queryByOrgId(Matchers.anyLong())).thenReturn(new ResponseEntity<>(stateMachineDTOS, HttpStatus.OK));
        Mockito.when(stateMachineFeignClient.removeStateMachineNode(Matchers.anyLong(), Matchers.anyLong(), Matchers.anyLong())).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        return stateMachineFeignClient;
    }

}
