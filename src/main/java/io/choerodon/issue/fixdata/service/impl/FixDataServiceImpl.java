package io.choerodon.issue.fixdata.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.service.*;
import io.choerodon.issue.domain.IssueType;
import io.choerodon.issue.domain.IssueTypeScheme;
import io.choerodon.issue.domain.Priority;
import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.issue.fixdata.dto.StatusForMoveDataDO;
import io.choerodon.issue.fixdata.feign.FixStateMachineFeignClient;
import io.choerodon.issue.infra.enums.SchemeApplyType;
import io.choerodon.issue.infra.enums.SchemeType;
import io.choerodon.issue.infra.mapper.IssueTypeMapper;
import io.choerodon.issue.infra.mapper.PriorityMapper;
import io.choerodon.issue.infra.mapper.StateMachineSchemeMapper;
import io.choerodon.issue.infra.utils.ProjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @date 2018/10/25
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class FixDataServiceImpl implements FixDataService {
    private static final Logger logger = LoggerFactory.getLogger(FixDataServiceImpl.class);
    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private FixStateMachineFeignClient fixStateMachineFeignClient;
    @Autowired
    private IssueTypeService issueTypeService;
    @Autowired
    private IssueTypeMapper issueTypeMapper;
    @Autowired
    private IssueTypeSchemeService issueTypeSchemeService;
    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private StateMachineSchemeMapper stateMachineSchemeMapper;
    @Autowired
    private PriorityService priorityService;
    @Autowired
    private PriorityMapper priorityMapper;

    @Override
    public void fixStateMachineScheme(List<StatusForMoveDataDO> statuses) {

        logger.info("开始修复状态");
        //创建状态
        fixStateMachineFeignClient.createStatus(statuses);
        logger.info("完成修复状态");
        //根据组织id分组
        Map<Long, List<StatusForMoveDataDO>> orgStatusMap = statuses.stream().collect(Collectors.groupingBy(StatusForMoveDataDO::getOrganizationId));
        for (Map.Entry<Long, List<StatusForMoveDataDO>> statusDOs : orgStatusMap.entrySet()) {
            Long organizationId = statusDOs.getKey();
            logger.info("开始修复组织{}", organizationId);
            //创建问题类型
            issueTypeService.initIssueTypeByConsumeCreateOrganization(organizationId);

            //创建优先级
            priorityService.initProrityByOrganization(Arrays.asList(organizationId));

            //根据项目id分组
            Map<Long, List<StatusForMoveDataDO>> proStatusMap = statusDOs.getValue().stream().collect(Collectors.groupingBy(StatusForMoveDataDO::getProjectId));
            for (Map.Entry<Long, List<StatusForMoveDataDO>> listEntry : proStatusMap.entrySet()) {
                Long projectId = listEntry.getKey();
                logger.info("开始修复项目{}", projectId);
                String projectCode = projectUtil.getCode(projectId);
                List<String> statusNames = listEntry.getValue().stream().map(StatusForMoveDataDO::getName).collect(Collectors.toList());
                //创建状态机
                Long stateMachineId = fixStateMachineFeignClient.createStateMachine(organizationId, projectCode, statusNames).getBody();
                //创建状态机方案
                StateMachineScheme stateMachineScheme = new StateMachineScheme();
                stateMachineScheme.setType(SchemeApplyType.AGILE);
                stateMachineScheme.setName(projectCode + "默认状态机方案");
                stateMachineScheme.setDescription(projectCode + "默认状态机方案");
                stateMachineScheme.setDefaultStateMachineId(stateMachineId);
                stateMachineScheme.setOrganizationId(organizationId);
                //保证幂等性
                List<StateMachineScheme> schemes = stateMachineSchemeMapper.select(stateMachineScheme);
                if (schemes.isEmpty()) {
                    int isInsert = stateMachineSchemeMapper.insert(stateMachineScheme);
                    if (isInsert != 1) {
                        throw new CommonException("error.stateMachineScheme.create");
                    }
                } else {
                    stateMachineScheme = schemes.get(0);
                }
                //创建与项目的关联关系
                projectConfigService.create(projectId, stateMachineScheme.getId(), SchemeType.STATE_MACHINE, SchemeApplyType.AGILE);

                //创建问题类型方案
                issueTypeSchemeService.initByConsumeCreateProject(projectId, projectCode);
                //创建项目信息及配置默认方案
                projectInfoService.createProject(projectId, projectCode);
                logger.info("完成修复项目{}", projectId);
            }
            logger.info("完成修复组织{}", organizationId);
        }
        logger.info("修复成功");
    }


    @Override
    public Map<Long, Map<String, Long>> queryPriorities() {
        List<Priority> priorities = priorityMapper.selectAll();
        Map<Long, List<Priority>> orgMaps = priorities.stream().collect(Collectors.groupingBy(Priority::getOrganizationId));
        Map<Long, Map<String, Long>> maps = new HashMap<>(orgMaps.size());
        for (Map.Entry<Long, List<Priority>> entry : orgMaps.entrySet()) {
            Map<String, Long> newMap = new HashMap<>(3);
            Map<String, Long> map = entry.getValue().stream().collect(Collectors.toMap(Priority::getName, Priority::getId));
            newMap.put("high", map.get("高"));
            newMap.put("medium", map.get("中"));
            newMap.put("low", map.get("低"));
            maps.put(entry.getKey(), newMap);
        }
        return maps;
    }

    @Override
    public Map<Long, Map<String, Long>> queryIssueTypes() {
        List<IssueType> issueTypes = issueTypeMapper.selectAll();
        Map<Long, List<IssueType>> orgMaps = issueTypes.stream().collect(Collectors.groupingBy(IssueType::getOrganizationId));
        Map<Long, Map<String, Long>> maps = new HashMap<>(orgMaps.size());
        for (Map.Entry<Long, List<IssueType>> entry : orgMaps.entrySet()) {
            Map<String, Long> map = entry.getValue().stream().collect(Collectors.toMap(IssueType::getTypeCode, IssueType::getId));
            maps.put(entry.getKey(), map);
        }
        return maps;
    }
}
