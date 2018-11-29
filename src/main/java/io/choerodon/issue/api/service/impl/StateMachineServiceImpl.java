package io.choerodon.issue.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.api.dto.payload.AddStatusWithProject;
import io.choerodon.issue.api.dto.payload.ChangeStatus;
import io.choerodon.issue.api.dto.payload.DeployStateMachinePayload;
import io.choerodon.issue.api.dto.payload.RemoveStatusWithProject;
import io.choerodon.issue.api.service.IssueService;
import io.choerodon.issue.api.service.StateMachineSchemeConfigService;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.api.service.StateMachineService;
import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.issue.domain.StateMachineSchemeConfig;
import io.choerodon.issue.infra.enums.CloopmCommonString;
import io.choerodon.issue.infra.enums.SchemeType;
import io.choerodon.issue.infra.feign.AgileFeignClient;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.StateMachineWithStatusDTO;
import io.choerodon.issue.infra.feign.dto.StatusDTO;
import io.choerodon.issue.infra.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;

/**
 * @author shinan.chen
 * @date 2018/9/25
 */
@Component
public class StateMachineServiceImpl implements StateMachineService {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineServiceImpl.class);
    @Value("${spring.application.name:default}")
    private String serverCode;
    @Autowired
    private StateMachineFeignClient stateMachineClient;
    @Autowired
    private StateMachineSchemeService schemeService;
    @Autowired
    private IssueService issueService;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private IssueRecordMapper issueRecordMapper;
    @Autowired
    private StateMachineSchemeMapper stateMachineSchemeMapper;
    @Autowired
    private AnalyzeServiceManager analyzeServiceManager;
    @Autowired
    private StateMachineSchemeConfigService stateMachineSchemeConfigService;
    @Autowired
    private ProjectConfigMapper projectConfigMapper;
    @Autowired
    private IssueTypeSchemeConfigMapper issueTypeSchemeConfigMapper;
    @Autowired
    private AgileFeignClient agileFeignClient;
    @Autowired
    private StateMachineSchemeConfigMapper configMapper;
    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;

    @Override
    public ResponseEntity<Page<StateMachineDTO>> pageQuery(Long organizationId, Integer page, Integer size, String[] sort, String name, String description, String[] param) {
        ResponseEntity<Page<StateMachineDTO>> responseEntity = stateMachineClient.pagingQuery(organizationId, page, size, sort, name, description, param);
        if (responseEntity != null && responseEntity.getBody() != null && responseEntity.getBody().getContent() != null) {
            for (StateMachineDTO stateMachineDTO : responseEntity.getBody().getContent()) {
                List<StateMachineSchemeDTO> list = schemeService.querySchemeByStateMachineId(organizationId, stateMachineDTO.getId());
                //列表去重
                List<StateMachineSchemeDTO> unique = list.stream().collect(
                        collectingAndThen(
                                toCollection(() -> new TreeSet<>(comparingLong(StateMachineSchemeDTO::getId))), ArrayList::new)
                );
                stateMachineDTO.setStateMachineSchemeDTOs(unique);
            }
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<Boolean> delete(Long organizationId, Long stateMachineId) {
        //有关联则无法删除，判断已发布的
        if (!stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId).isEmpty()) {
            throw new CommonException("error.stateMachine.delete");
        }
        //删除草稿的已关联当前状态机【todo】

        ResponseEntity<StateMachineDTO> responseEntity = stateMachineClient.queryStateMachineById(organizationId, stateMachineId);
        if (responseEntity == null || responseEntity.getBody() == null) {
            throw new CommonException("error.stateMachine.delete.noFound");
        }
        return stateMachineClient.delete(organizationId, stateMachineId);
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long stateMachineId) {
        Map<String, Object> map = new HashMap<>();
        ResponseEntity<StateMachineDTO> responseEntity = stateMachineClient.queryStateMachineById(organizationId, stateMachineId);
        if (responseEntity == null || responseEntity.getBody() == null) {
            map.put(CloopmCommonString.CAN_DELETE, false);
            map.put("reason", "noFound");
            return map;
        }
        List<Long> schemeIds = stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);
        if (schemeIds.isEmpty()) {
            map.put(CloopmCommonString.CAN_DELETE, true);
        } else {
            map.put(CloopmCommonString.CAN_DELETE, false);
            map.put("schemeUsed", schemeIds.size());
        }
        return map;
    }

    @Override
    public Map<String, Object> checkDeleteNode(Long organizationId, Long stateMachineId, Long statusId) {
        //找到与状态机关联的状态机方案
        List<Long> schemeIds = stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);
        List<ProjectConfig> projectConfigs = new ArrayList<>();
        schemeIds.forEach(schemeId -> {
            //获取当前方案配置的项目列表
            projectConfigs.addAll(projectConfigMapper.queryConfigsBySchemeId(SchemeType.STATE_MACHINE, schemeId));
        });
        Map<String, Object> result = agileFeignClient.checkDeleteNode(organizationId, statusId, projectConfigs).getBody();
        return result;
    }

    @Override
    public void notActiveStateMachine(Long organizationId, List<Long> stateMachineIds) {
        List<Long> notActiveStateMachineIds = new ArrayList<>();
        if (!stateMachineIds.isEmpty()) {
            //校验去掉仍然有关联方案的状态机
            List<StateMachineSchemeConfig> configs = configMapper.queryByStateMachineIds(organizationId, stateMachineIds);
            Map<Long, List<StateMachineSchemeConfig>> configMap = configs.stream().collect(Collectors.groupingBy(StateMachineSchemeConfig::getStateMachineId));
            stateMachineIds.forEach(stateMachineId -> {
                List<StateMachineSchemeConfig> configList = configMap.get(stateMachineId);
                if (configList == null || configList.isEmpty()) {
                    notActiveStateMachineIds.add(stateMachineId);
                }
            });
            //使活跃的状态机变更为未活跃
            logger.info("notActiveStateMachine: {}", stateMachineIds.toString());
            if (!stateMachineIds.isEmpty()) {
                stateMachineClient.notActiveStateMachines(organizationId, notActiveStateMachineIds);
            }
        }
    }

    @Override
    public DeployStateMachinePayload handleStateMachineChangeStatusByStateMachineId(Long organizationId, Long stateMachineId, ChangeStatus changeStatus) {
        //找到与状态机关联的状态机方案
        List<Long> schemeIds = stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);
        DeployStateMachinePayload deployStateMachinePayload = handleStateMachineChangeStatusBySchemeIds(organizationId, stateMachineId, schemeIds, changeStatus);
        return deployStateMachinePayload;
    }

    @Override
    public DeployStateMachinePayload handleStateMachineChangeStatusBySchemeIds(Long organizationId, Long stateMachineId, List<Long> schemeIds, ChangeStatus changeStatus) {
        if (schemeIds == null || schemeIds.isEmpty()) {
            return null;
        }
        DeployStateMachinePayload deployStateMachinePayload = new DeployStateMachinePayload();
        List<RemoveStatusWithProject> removeStatusWithProjects = new ArrayList<>();
        List<AddStatusWithProject> addStatusWithProjects = new ArrayList<>();
        List<Long> deleteStatusIds = changeStatus.getDeleteStatusIds();
        List<Long> addStatusIds = changeStatus.getAddStatusIds();

        //获取所有状态机及状态机的状态列表
        List<StateMachineWithStatusDTO> stateMachineWithStatusDTOs = stateMachineFeignClient.queryAllWithStatus(organizationId).getBody();
        Map<Long, List<StatusDTO>> stateMachineWithStatusDTOsMap = stateMachineWithStatusDTOs.stream().collect(Collectors.toMap(StateMachineWithStatusDTO::getId, StateMachineWithStatusDTO::getStatusDTOS));
        //查出组织下所有状态机方案配置
        List<StateMachineSchemeConfig> schemeConfigs = configMapper.queryByOrgId(organizationId);
        Map<Long, List<StateMachineSchemeConfig>> schemeConfigsMap = schemeConfigs.stream().collect(Collectors.groupingBy(StateMachineSchemeConfig::getSchemeId));

        //根据方案列表查出每个项目关联的状态机
        List<ProjectConfig> projectConfigs = projectConfigMapper.handleRemoveStatus(schemeIds, SchemeType.STATE_MACHINE);
        Map<Long, List<ProjectConfig>> projectMap = projectConfigs.stream().collect(Collectors.groupingBy(ProjectConfig::getProjectId));
        projectMap.entrySet().forEach(entry -> {
            Long projectId = entry.getKey();
            List<ProjectConfig> projectConfigsList = entry.getValue();
            List<StatusDTO> statuses = new ArrayList<>();
            projectConfigsList.forEach(projectConfig -> {
                schemeConfigsMap.get(projectConfig.getSchemeId()).forEach(schemeConfig -> {
                    Long smId = schemeConfig.getStateMachineId();
                    //排除当前修改的状态机，要判断当前项目下其他状态机是否有当前要判断的状态
                    if (!smId.equals(stateMachineId)) {
                        List<StatusDTO> statusDTOS = stateMachineWithStatusDTOsMap.get(smId);
                        statuses.addAll(statusDTOS);
                    }
                });
            });
            List<Long> statusIds = statuses.stream().map(StatusDTO::getId).distinct().collect(Collectors.toList());
            //取当前项目真正减少的状态
            List<Long> confirmDeleteStatusIds = deleteStatusIds.stream().filter(x -> !statusIds.contains(x)).collect(toList());
            //取当前项目真正增加的状态
            List<Long> confirmAddStatusIds = addStatusIds.stream().filter(x -> !schemeIds.contains(x)).collect(toList());

            if (!confirmDeleteStatusIds.isEmpty()) {
                RemoveStatusWithProject removeStatusWithProject = new RemoveStatusWithProject();
                removeStatusWithProject.setProjectId(projectId);
                removeStatusWithProject.setDeleteStatusIds(confirmDeleteStatusIds);
                removeStatusWithProjects.add(removeStatusWithProject);
            }
            if (!confirmAddStatusIds.isEmpty()) {
                AddStatusWithProject addStatusWithProject = new AddStatusWithProject();
                addStatusWithProject.setProjectId(projectId);
                addStatusWithProject.setAddStatusIds(confirmAddStatusIds);
                addStatusWithProjects.add(addStatusWithProject);
            }
        });
        deployStateMachinePayload.setRemoveStatusWithProjects(removeStatusWithProjects);
        deployStateMachinePayload.setAddStatusWithProjects(addStatusWithProjects);
        return deployStateMachinePayload;
    }
}
