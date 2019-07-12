package io.choerodon.issue.api.config;


import com.github.pagehelper.PageInfo;
import io.choerodon.asgard.saga.dto.SagaInstanceDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.issue.api.vo.ExecuteResult;
import io.choerodon.issue.api.vo.InputVO;
import io.choerodon.issue.api.vo.payload.StateMachineSchemeDeployCheckIssue;
import io.choerodon.issue.api.vo.payload.TransformInfo;
import io.choerodon.issue.app.service.impl.InitServiceImpl;
import io.choerodon.issue.app.service.impl.SagaServiceImpl;
import io.choerodon.issue.infra.enums.TransformConditionStrategy;
import io.choerodon.issue.infra.enums.TransformType;
import io.choerodon.issue.infra.feign.AgileFeignClient;
import io.choerodon.issue.infra.feign.CustomFeignClientAdaptor;
import io.choerodon.issue.infra.feign.UserFeignClient;
import io.choerodon.issue.infra.feign.fallback.AgileFeignClientFallback;
import io.choerodon.issue.infra.feign.fallback.CustomFeignClientAdaptorFallBack;
import io.choerodon.issue.infra.feign.fallback.UserFeignClientFallback;
import io.choerodon.issue.infra.feign.vo.ProjectVO;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/11/12
 */
@Configuration
public class MockConfiguration {
    @Bean
    public SagaClient sagaClient() {
        SagaClient sagaClient = Mockito.mock(SagaClient.class);
        Mockito.when(sagaClient.startSaga(Matchers.anyString(), Matchers.any(StartInstanceDTO.class))).thenReturn(new SagaInstanceDTO());
        SagaServiceImpl sagaService = ApplicationContextHelper.getSpringFactory().getBean(SagaServiceImpl.class);
        sagaService.setSagaClient(sagaClient);
        InitServiceImpl initService = ApplicationContextHelper.getSpringFactory().getBean(InitServiceImpl.class);
        initService.setSagaClient(sagaClient);
        return sagaClient;
    }

    @Bean
    @Primary
    CustomFeignClientAdaptor customFeignClientAdaptor() {
        CustomFeignClientAdaptor customFeignClientAdaptor = Mockito.mock(CustomFeignClientAdaptorFallBack.class);
        ExecuteResult executeResult = new ExecuteResult();
        executeResult.setSuccess(true);
        executeResult.setResultStatusId(1L);
        Mockito.when(customFeignClientAdaptor.executeConfig(Matchers.any(URI.class), Matchers.any(InputVO.class))).thenReturn(new ResponseEntity(executeResult, HttpStatus.OK));
        List<TransformInfo> transformInfos = new ArrayList<>();
        TransformInfo transformInfo = new TransformInfo();
        transformInfo.setId(10L);
        transformInfo.setOrganizationId(1L);
        transformInfo.setName("新转换");
        transformInfo.setType(TransformType.ALL);
        transformInfo.setConditionStrategy(TransformConditionStrategy.ALL);
        transformInfo.setStateMachineId(10L);
        transformInfo.setEndStatusId(10L);
        transformInfo.setStartStatusId(0L);
        transformInfos.add(transformInfo);
        Mockito.when(customFeignClientAdaptor.filterTransformsByConfig(Matchers.any(URI.class), Matchers.any(ArrayList.class))).thenReturn(new ResponseEntity(transformInfos, HttpStatus.OK));
        return customFeignClientAdaptor;
    }

    @Bean
    @Primary
    UserFeignClient userFeignClient() {
        UserFeignClient userFeignClient = Mockito.mock(UserFeignClientFallback.class);
        ProjectVO projectVO = new ProjectVO();
        projectVO.setId(1L);
        projectVO.setName("test");
        projectVO.setOrganizationId(1L);
        Mockito.when(userFeignClient.queryProject(Matchers.anyLong())).thenReturn(new ResponseEntity<>(projectVO, HttpStatus.OK));
        PageInfo<ProjectVO> projectVOS = new PageInfo<>();
        List<ProjectVO> projectVOList = new ArrayList<>(1);
        projectVOList.add(projectVO);
        projectVOS.setList(projectVOList);
        projectVOS.setPageSize(1);
        projectVOS.setSize(1);
        Mockito.when(userFeignClient.queryProjectsByOrgId(Matchers.anyLong(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(new ResponseEntity<>(projectVOS, HttpStatus.OK));
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
        Map<String, Object> result = new HashMap<>(2);
        Map<Long, Long> map2 = new HashMap<>();
        result.put("canDelete", true);
        result.put("count", 0);
        map2.put(1L, 1L);
        Mockito.when(agileFeignClient.checkDeleteNode(Matchers.anyLong(), Matchers.anyLong(), Matchers.any(List.class))).thenReturn(new ResponseEntity<>(result, HttpStatus.OK));
        Mockito.when(agileFeignClient.checkStateMachineSchemeChange(Matchers.anyLong(), Matchers.any(StateMachineSchemeDeployCheckIssue.class))).thenReturn(new ResponseEntity<>(map2, HttpStatus.OK));
        return agileFeignClient;
    }
}
