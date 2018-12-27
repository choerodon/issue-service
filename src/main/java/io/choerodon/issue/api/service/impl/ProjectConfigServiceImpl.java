package io.choerodon.issue.api.service.impl;


import com.alibaba.fastjson.JSON;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.issue.api.dto.*;
import io.choerodon.issue.api.dto.payload.StatusPayload;
import io.choerodon.issue.api.service.IssueTypeSchemeService;
import io.choerodon.issue.api.service.ProjectConfigService;
import io.choerodon.issue.api.service.StateMachineSchemeConfigService;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.domain.IssueType;
import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.issue.infra.enums.SchemeApplyType;
import io.choerodon.issue.infra.enums.SchemeType;
import io.choerodon.issue.infra.exception.RemoveStatusException;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.StatusDTO;
import io.choerodon.issue.infra.feign.dto.TransformDTO;
import io.choerodon.issue.infra.mapper.IssueTypeMapper;
import io.choerodon.issue.infra.mapper.IssueTypeSchemeConfigMapper;
import io.choerodon.issue.infra.mapper.IssueTypeSchemeMapper;
import io.choerodon.issue.infra.mapper.ProjectConfigMapper;
import io.choerodon.issue.infra.utils.EnumUtil;
import io.choerodon.issue.infra.utils.ProjectUtil;
import io.choerodon.issue.statemachine.fegin.InstanceFeignClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author shinan.chen
 * @Date 2018/10/24
 */
@Component
@RefreshScope
public class ProjectConfigServiceImpl implements ProjectConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectConfigServiceImpl.class);
    private static final String YES = "1";
    private static final String AGILE_SERVICE = "agile-service";
    private static final String FLAG = "flag";
    private static final String MESSAGE = "message";
    private static final String STATEMACHINEID = "stateMachineId";
    private static final String ERROR_ISSUE_STATE_MACHINE_NOT_FOUND = "error.issueStateMachine.notFound";
    private static final String ERROR_ISSUE_STATUS_NOT_FOUND = "error.createIssue.issueStatusNotFound";

    @Autowired
    private ProjectConfigMapper projectConfigMapper;
    @Autowired
    private IssueTypeMapper issueTypeMapper;
    @Autowired
    private IssueTypeSchemeMapper issueTypeSchemeMapper;
    @Autowired
    private IssueTypeSchemeConfigMapper issueTypeSchemeConfigMapper;
    @Autowired
    private StateMachineSchemeConfigService stateMachineSchemeConfigService;
    @Autowired
    private IssueTypeSchemeService issueTypeSchemeService;
    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;
    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;
    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private InstanceFeignClient instanceFeignClient;
    @Autowired
    private SagaClient sagaClient;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public ProjectConfig create(Long projectId, Long schemeId, String schemeType, String applyType) {
        if (!EnumUtil.contain(SchemeType.class, schemeType)) {
            throw new CommonException("error.schemeType.illegal");
        }
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        ProjectConfig projectConfig = new ProjectConfig(projectId, schemeId, schemeType, applyType);
        //保证幂等性
        List<ProjectConfig> configs = projectConfigMapper.select(projectConfig);
        if (!configs.isEmpty()) {
            return configs.get(0);
        }
        int result = projectConfigMapper.insert(projectConfig);
        if (result != 1) {
            throw new CommonException("error.projectConfig.create");
        }

        //若是关联状态机方案，设置状态机方案、状态机为活跃
        if (schemeType.equals(SchemeType.STATE_MACHINE)) {
            stateMachineSchemeService.activeSchemeWithRefProjectConfig(schemeId);
        }
        return projectConfig;
    }

    @Override
    public ProjectConfigDetailDTO queryById(Long projectId) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        List<ProjectConfig> projectConfigs = projectConfigMapper.queryByProjectId(projectId);
        Map<String, List<ProjectConfig>> configMap = projectConfigs.stream().collect(Collectors.groupingBy(ProjectConfig::getSchemeType));
        ProjectConfigDetailDTO projectConfigDetailDTO = new ProjectConfigDetailDTO();
        projectConfigDetailDTO.setProjectId(projectId);
        //获取问题类型方案
        List<ProjectConfig> issueTypeSchemeConfigs = configMap.get(SchemeType.ISSUE_TYPE);
        if (issueTypeSchemeConfigs != null && !issueTypeSchemeConfigs.isEmpty()) {
            Map<String, IssueTypeSchemeDTO> issueTypeSchemeMap = new HashMap<>(issueTypeSchemeConfigs.size());
            for (ProjectConfig projectConfig : issueTypeSchemeConfigs) {
                IssueTypeSchemeDTO issueTypeSchemeDTO = issueTypeSchemeService.queryById(organizationId, projectConfig.getSchemeId());
                issueTypeSchemeMap.put(projectConfig.getApplyType(), issueTypeSchemeDTO);
            }
            projectConfigDetailDTO.setIssueTypeSchemeMap(issueTypeSchemeMap);
        }
        //获取状态机方案
        List<ProjectConfig> stateMachineSchemeConfigs = configMap.get(SchemeType.STATE_MACHINE);
        if (stateMachineSchemeConfigs != null && !stateMachineSchemeConfigs.isEmpty()) {
            Map<String, StateMachineSchemeDTO> stateMachineSchemeMap = new HashMap<>(stateMachineSchemeConfigs.size());
            for (ProjectConfig projectConfig : stateMachineSchemeConfigs) {
                StateMachineSchemeDTO stateMachineSchemeDTO = stateMachineSchemeService.querySchemeWithConfigById(false, organizationId, projectConfig.getSchemeId());
                stateMachineSchemeMap.put(projectConfig.getApplyType(), stateMachineSchemeDTO);
            }
            projectConfigDetailDTO.setStateMachineSchemeMap(stateMachineSchemeMap);
        }
        return projectConfigDetailDTO;
    }

    @Override
    public List<IssueTypeDTO> queryIssueTypesByProjectId(Long projectId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        Long organizationId = projectUtil.getOrganizationId(projectId);
        ProjectConfig projectConfig = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.ISSUE_TYPE, applyType);
        //获取问题类型方案
        if (projectConfig.getSchemeId() != null) {
            //根据方案配置表获取 问题类型
            List<IssueType> issueTypes = issueTypeMapper.queryBySchemeId(organizationId, projectConfig.getSchemeId());
            return modelMapper.map(issueTypes, new TypeToken<List<IssueTypeDTO>>() {
            }.getType());
        } else {
            throw new CommonException("error.queryIssueTypesByProjectId.issueTypeSchemeId.null");
        }
    }

    @Override
    public List<IssueTypeWithStateMachineIdDTO> queryIssueTypesWithStateMachineIdByProjectId(Long projectId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long issueTypeSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.ISSUE_TYPE, applyType).getSchemeId();
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (issueTypeSchemeId == null) {
            throw new CommonException("error.issueTypeSchemeId.null");
        }
        if (stateMachineSchemeId == null) {
            throw new CommonException("error.stateMachineSchemeId.null");
        }
        //根据方案配置表获取 问题类型
        List<IssueType> issueTypes = issueTypeMapper.queryBySchemeId(organizationId, issueTypeSchemeId);
        //根据方案配置表获取 状态机与问题类型的对应关系
        List<StateMachineSchemeConfigDTO> configs = stateMachineSchemeConfigService.queryBySchemeId(false, organizationId, stateMachineSchemeId);
        Map<Long, Long> map = configs.stream().collect(Collectors.toMap(StateMachineSchemeConfigDTO::getIssueTypeId, StateMachineSchemeConfigDTO::getStateMachineId));
        Long defaultStateMachineId = stateMachineSchemeConfigService.selectDefault(false, organizationId, stateMachineSchemeId).getStateMachineId();
        List<IssueTypeWithStateMachineIdDTO> issueTypeWithStateMachineIds = modelMapper.map(issueTypes, new TypeToken<List<IssueTypeWithStateMachineIdDTO>>() {
        }.getType());
        issueTypeWithStateMachineIds.forEach(x -> {
            Long stateMachineId = map.get(x.getId());
            if (stateMachineId != null) {
                x.setStateMachineId(stateMachineId);
            } else {
                x.setStateMachineId(defaultStateMachineId);
            }
        });
        return issueTypeWithStateMachineIds;
    }

    @Override
    public List<StatusDTO> queryStatusByIssueTypeId(Long projectId, Long issueTypeId, String applyType) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (stateMachineSchemeId == null) {
            throw new CommonException("error.stateMachineSchemeId.null");
        }
        //获取状态机
        Long stateMachineId = stateMachineSchemeConfigService.queryStateMachineIdBySchemeIdAndIssueTypeId(false, organizationId, stateMachineSchemeId, issueTypeId);
        return stateMachineFeignClient.queryByStateMachineIds(organizationId, Collections.singletonList(stateMachineId)).getBody();
    }

    @Override
    public List<StatusDTO> queryStatusByProjectId(Long projectId, String applyType) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (stateMachineSchemeId == null) {
            throw new CommonException("error.stateMachineSchemeId.null");
        }
        //获取状态机ids
        List<Long> stateMachineIds = stateMachineSchemeConfigService.queryBySchemeId(false, organizationId, stateMachineSchemeId)
                .stream().map(StateMachineSchemeConfigDTO::getStateMachineId).collect(Collectors.toList());
        return stateMachineFeignClient.queryByStateMachineIds(organizationId, stateMachineIds).getBody();
    }

    @Override
    public List<TransformDTO> queryTransformsByProjectId(Long projectId, Long currentStatusId, Long issueId, Long issueTypeId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        Long organizationId = projectUtil.getOrganizationId(projectId);
        ProjectConfig projectConfig = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType);
        //获取状态机方案
        if (projectConfig.getSchemeId() != null) {
            //获取状态机
            Long stateMachineId = stateMachineSchemeConfigService.queryStateMachineIdBySchemeIdAndIssueTypeId(false, organizationId, projectConfig.getSchemeId(), issueTypeId);
            //获取当前状态拥有的转换
            List<TransformDTO> transformDTOS = stateMachineFeignClient.transformList(organizationId, AGILE_SERVICE, stateMachineId, issueId, currentStatusId).getBody();
            //获取组织中所有状态
            List<StatusDTO> statusDTOS = stateMachineFeignClient.queryAllStatus(organizationId).getBody();
            Map<Long, StatusDTO> statusMap = statusDTOS.stream().collect(Collectors.toMap(StatusDTO::getId, x -> x));
            transformDTOS.forEach(transformDTO -> {
                StatusDTO statusDTO = statusMap.get(transformDTO.getEndStatusId());
                transformDTO.setStatusDTO(statusDTO);
            });
            //如果转换中不包含当前状态，则添加一个self
            if (transformDTOS.stream().noneMatch(transformDTO -> currentStatusId.equals(transformDTO.getEndStatusId()))) {
                TransformDTO self = new TransformDTO();
                self.setEndStatusId(currentStatusId);
                self.setStatusDTO(statusMap.get(currentStatusId));
                transformDTOS.add(self);
            }
            return transformDTOS;
        } else {
            throw new CommonException("error.queryIssueTypesByProjectId.issueTypeSchemeId.null");
        }
    }

    @Override
    public Long queryStateMachineId(Long projectId, String applyType, Long issueTypeId) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long issueTypeSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.ISSUE_TYPE, applyType).getSchemeId();
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (issueTypeSchemeId == null) {
            throw new CommonException("error.queryStateMachineId.issueTypeSchemeId.null");
        }
        if (stateMachineSchemeId == null) {
            throw new CommonException("error.queryStateMachineId.getStateMachineSchemeId.null");
        }
        return stateMachineSchemeConfigService.queryStateMachineIdBySchemeIdAndIssueTypeId(false, organizationId, stateMachineSchemeId, issueTypeId);
    }

    @Override
    public StatusDTO createStatusForAgile(Long projectId, StatusDTO statusDTO) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        statusDTO.setOrganizationId(organizationId);
        Map<String, Object> result = checkCreateStatusForAgile(projectId);
        if ((Boolean) result.get(FLAG)) {
            Long stateMachineId = (Long) result.get(STATEMACHINEID);
            statusDTO = stateMachineFeignClient.createStatusForAgile(organizationId, stateMachineId, statusDTO).getBody();
        } else {
            throw new CommonException((String) result.get(MESSAGE));
        }
        return statusDTO;
    }

    @Override
    public Map<String, Object> checkCreateStatusForAgile(Long projectId) {
        Map<String, Object> result = new HashMap<>(3);
        result.put(FLAG, true);
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, SchemeApplyType.AGILE).getSchemeId();
        //校验状态机方案是否只关联一个项目
        ProjectConfig select = new ProjectConfig();
        select.setSchemeId(stateMachineSchemeId);
        select.setSchemeType(SchemeType.STATE_MACHINE);
        select.setApplyType(SchemeApplyType.AGILE);
        if (projectConfigMapper.select(select).size() > 1) {
            result.put(FLAG, false);
            result.put(MESSAGE, "error.stateMachineScheme.multiScheme");
            return result;
        }
        //校验状态机方案是否只有一个状态机
        if (stateMachineSchemeConfigService.queryBySchemeId(false, organizationId, stateMachineSchemeId).size() > 1) {
            result.put(FLAG, false);
            result.put(MESSAGE, "error.stateMachineScheme.multiStateMachine");
            return result;
        }
        Long stateMachineId = stateMachineSchemeConfigService.selectDefault(false, organizationId, stateMachineSchemeId).getStateMachineId();
        if (stateMachineId == null) {
            result.put(FLAG, false);
            result.put(MESSAGE, "error.stateMachineScheme.defaultStateMachineId.notNull");
            return result;
        }
        //校验这个状态机是否只关联一个方案
        List<Long> schemeIds = stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);
        if (schemeIds.size() > 1) {
            result.put(FLAG, false);
            result.put(MESSAGE, "error.stateMachineScheme.stateMachineInMoreThanOneScheme");
            return result;
        }
        result.put(STATEMACHINEID, stateMachineId);
        return result;
    }

    @Saga(code = "agile-remove-status", description = "移除状态", inputSchemaClass = StatusPayload.class)
    @Override
    public void removeStatusForAgile(Long projectId, Long statusId) {
        Map<String, Object> result = checkCreateStatusForAgile(projectId);
        Boolean flag = (Boolean) result.get(FLAG);
        if (flag) {
            Long stateMachineId = (Long) result.get(STATEMACHINEID);
            Long organizationId = projectUtil.getOrganizationId(projectId);
            Long initStatusId = stateMachineFeignClient.queryInitStatusId(organizationId, stateMachineId).getBody();
            if (statusId.equals(initStatusId)) {
                throw new CommonException("error.initStatus.illegal");
            }
            try {
                ResponseEntity responseEntity = stateMachineFeignClient.removeStateMachineNode(organizationId, stateMachineId, statusId);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    StatusPayload statusPayload = new StatusPayload();
                    statusPayload.setProjectId(projectId);
                    statusPayload.setStatusId(statusId);
                    sagaClient.startSaga("agile-remove-status", new StartInstanceDTO(JSON.toJSONString(statusPayload), "", "", ResourceLevel.PROJECT.value(), projectId));
                }
            } catch (Exception e) {
                throw new RemoveStatusException("error.status.remove");
            }
        } else {
            throw new RemoveStatusException((String) result.get(MESSAGE));
        }
    }

    @Override
    public Boolean checkRemoveStatusForAgile(Long projectId, Long statusId) {
        Map<String, Object> result = checkCreateStatusForAgile(projectId);
        Boolean flag = (Boolean) result.get(FLAG);
        if (flag) {
            Long stateMachineId = (Long) result.get(STATEMACHINEID);
            Long organizationId = projectUtil.getOrganizationId(projectId);
            Long initStatusId = stateMachineFeignClient.queryInitStatusId(organizationId, stateMachineId).getBody();
            return !statusId.equals(initStatusId);
        } else {
            throw new RemoveStatusException((String) result.get(MESSAGE));
        }
    }

    @Override
    public Map<String, List<Long>> queryProjectIdsMap(Long organizationId, Long stateMachineId) {
        //查询状态机方案中的配置
        List<Long> schemeIds = stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);

        if (!schemeIds.isEmpty()) {
            List<ProjectConfig> projectConfigs = projectConfigMapper.queryBySchemeIds(schemeIds, SchemeType.STATE_MACHINE);
            Map<String, List<Long>> projectIdsMap = projectConfigs.stream().collect(Collectors.groupingBy(ProjectConfig::getApplyType, Collectors.mapping(ProjectConfig::getProjectId, Collectors.toList())));
            return projectIdsMap;
        }
        return Collections.emptyMap();
    }

    @Override
    public Long queryWorkFlowFirstStatus(Long projectId, String applyType, Long issueTypeId, Long organizationId) {
        Long statusMachineId = projectConfigService.queryStateMachineId(projectId, applyType, issueTypeId);
        if (statusMachineId == null) {
            throw new CommonException(ERROR_ISSUE_STATE_MACHINE_NOT_FOUND);
        }
        Long initStatusId = instanceFeignClient.queryInitStatusId(organizationId, statusMachineId).getBody();
        if (initStatusId == null) {
            throw new CommonException(ERROR_ISSUE_STATUS_NOT_FOUND);
        }
        return initStatusId;
    }
}
