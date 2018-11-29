package io.choerodon.issue.api.service.impl;

import com.alibaba.fastjson.JSON;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.issue.api.dto.payload.*;
import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.issue.infra.enums.SchemeType;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.StatusDTO;
import io.choerodon.issue.infra.mapper.ProjectConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @date 2018/11/29
 */
@Component
public class SagaServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(SagaServiceImpl.class);
    private static final String DEPLOY_STATE_MACHINE_SCHEME = "deploy-state-machine-scheme";

    @Autowired
    private SagaClient sagaClient;
    @Autowired
    private StateMachineServiceImpl stateMachineService;
    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;
    @Autowired
    private ProjectConfigMapper projectConfigMapper;

    @Saga(code = DEPLOY_STATE_MACHINE_SCHEME, description = "issue服务发布状态机方案", inputSchemaClass = StateMachineSchemeDeployUpdateIssue.class)
    public void deployStateMachineScheme(Long organizationId, Long schemeId, List<StateMachineSchemeChangeItem> changeItems, ChangeStatus changeStatus) {
        //获取当前方案配置的项目列表
        List<ProjectConfig> projectConfigs = projectConfigMapper.queryConfigsBySchemeId(SchemeType.STATE_MACHINE, schemeId);
        //获取所有状态
        List<StatusDTO> statusDTOS = stateMachineFeignClient.queryAllStatus(organizationId).getBody();
        Map<Long, StatusDTO> statusDTOMap = statusDTOS.stream().collect(Collectors.toMap(StatusDTO::getId, x -> x));
        //将要增加和减少的状态进行判断，确定哪些项目要增加哪些状态与减少哪些状态
        DeployStateMachinePayload deployStateMachinePayload = stateMachineService.handleStateMachineChangeStatusBySchemeIds(organizationId, null, schemeId, Arrays.asList(schemeId), changeStatus);
        List<RemoveStatusWithProject> removeStatusWithProjects = deployStateMachinePayload.getRemoveStatusWithProjects();
        List<AddStatusWithProject> addStatusWithProjects = deployStateMachinePayload.getAddStatusWithProjects();
        //新增的状态赋予实体
        deployStateMachinePayload.getAddStatusWithProjects().forEach(addStatusWithProject -> {
            List<StatusDTO> statuses = new ArrayList<>(addStatusWithProject.getAddStatusIds().size());
            addStatusWithProject.getAddStatusIds().forEach(addStatusId -> {
                StatusDTO status = statusDTOMap.get(addStatusId);
                if (status != null) {
                    statuses.add(status);
                }
            });
            addStatusWithProject.setAddStatuses(statuses);
        });
        //发送saga，批量更新issue的状态，并对相应的项目进行状态的增加与减少
        StateMachineSchemeDeployUpdateIssue deployUpdateIssue = new StateMachineSchemeDeployUpdateIssue();
        deployUpdateIssue.setChangeItems(changeItems);
        deployUpdateIssue.setProjectConfigs(projectConfigs);
        deployUpdateIssue.setAddStatusWithProjects(addStatusWithProjects);
        deployUpdateIssue.setRemoveStatusWithProjects(removeStatusWithProjects);
        deployUpdateIssue.setSchemeId(schemeId);
        deployUpdateIssue.setOrganizationId(organizationId);
        deployUpdateIssue.setUserId(DetailsHelper.getUserDetails().getUserId());
        sagaClient.startSaga(DEPLOY_STATE_MACHINE_SCHEME, new StartInstanceDTO(JSON.toJSONString(deployUpdateIssue), "", ""));
        logger.info("startSaga deploy-state-machine-scheme addStatusIds: {}, deleteStatusIds: {}", changeStatus.getAddStatusIds(), changeStatus.getDeleteStatusIds());
    }
}
