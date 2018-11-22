package io.choerodon.issue.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.api.service.IssueService;
import io.choerodon.issue.api.service.StateMachineSchemeConfigService;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.api.service.StateMachineService;
import io.choerodon.issue.domain.IssueTypeSchemeConfig;
import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.issue.infra.enums.CloopmCommonString;
import io.choerodon.issue.infra.enums.SchemeType;
import io.choerodon.issue.infra.feign.AgileFeignClient;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author shinan.chen
 * @date 2018/9/25
 */
@Component
public class StateMachineServiceImpl implements StateMachineService {

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
        //返回结果，每个项目的哪些问题类型
        Map<Long, List<Long>> issueTypeIdsMap = new HashMap<>();
        //查询出所有问题类型方案配置
        IssueTypeSchemeConfig issueTypeSchemeConfig = new IssueTypeSchemeConfig();
        issueTypeSchemeConfig.setOrganizationId(organizationId);
        Map<Long, List<Long>> issueTypeSchemeConfigMap = issueTypeSchemeConfigMapper.select(issueTypeSchemeConfig).stream().collect(Collectors.groupingBy(IssueTypeSchemeConfig::getSchemeId, Collectors.mapping(IssueTypeSchemeConfig::getIssueTypeId, Collectors.toList())));
        //找到用到状态机的方案
        List<Long> schemeIds = stateMachineSchemeConfigService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);
        List<StateMachineScheme> schemes = stateMachineSchemeMapper.queryByIds(organizationId, schemeIds);
        //找出方案所在组织的所有配置
        List<Long> projectIds = projectConfigMapper.queryBySchemeIds(schemeIds, SchemeType.STATE_MACHINE).stream().map(ProjectConfig::getProjectId).collect(Collectors.toList());
        if (!projectIds.isEmpty()) {
            List<ProjectConfig> projectConfigs = projectConfigMapper.queryByProjectIds(projectIds);
            //projectId+appltType Map
            Map<String, ProjectConfig> paMap = projectConfigs.stream().collect(Collectors.toMap(x -> x.getProjectId() + ":" + x.getSchemeType() + ":" + x.getApplyType(), x -> x));
            Map<String, List<ProjectConfig>> schemeTypeMap = projectConfigs.stream().collect(Collectors.groupingBy(ProjectConfig::getSchemeType));
            //根据状态机方案id分类
            Map<Long, List<ProjectConfig>> schemeIdSMMap = schemeTypeMap.get(SchemeType.STATE_MACHINE).stream().collect(Collectors.groupingBy(ProjectConfig::getSchemeId));
            schemeIds.forEach(schemeId -> {
                //查询出方案下与状态机关联的问题类型
                List<Long> issueTypeIds = stateMachineSchemeConfigService.queryIssueTypeIdBySchemeIdAndStateMachineId(false, organizationId, schemeId, stateMachineId);
                //查询出配置该方案的项目列表
                List<ProjectConfig> projectConfigList = schemeIdSMMap.get(schemeId);
                //该状态机是默认配置，找到与方案匹配的问题类型方案
                for (ProjectConfig projectConfig : projectConfigList) {
                    Long projectId = projectConfig.getProjectId();
                    String key = projectId + ":" + SchemeType.ISSUE_TYPE + ":" + projectConfig.getApplyType();
                    ProjectConfig issueTypeSchemeProjectConfig = paMap.get(key);
                    List<Long> issueTypeIdsAll = issueTypeSchemeConfigMap.get(issueTypeSchemeProjectConfig.getSchemeId());
                    //当前状态机在该方案下匹配所有未配置的问题类型
                    if (issueTypeIds.contains(0L)) {
                        putResult(issueTypeIdsMap, projectId, issueTypeIdsAll);
                    } else {
                        List<Long> checkIds = issueTypeIds.stream().filter(issueTypeId -> issueTypeIdsAll.contains(issueTypeId)).collect(Collectors.toList());
                        putResult(issueTypeIdsMap, projectId, checkIds);
                    }
                }
            });
        }
        Map<String, Object> result = agileFeignClient.checkDeleteNode(organizationId, statusId, issueTypeIdsMap).getBody();
        return result;
    }

    /**
     * 设置每个项目要校验的问题类型id列表
     *
     * @param result
     * @param projectId
     * @param issueTypeIds
     */
    private void putResult(Map<Long, List<Long>> result, Long projectId, List<Long> issueTypeIds) {
        List<Long> ids = result.get(projectId);
        if (ids == null) {
            ids = new ArrayList<>();
        }
        ids.addAll(issueTypeIds);
        result.put(projectId, ids);
    }
}
