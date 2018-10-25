package io.choerodon.issue.fixdata.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.service.FixDataService;
import io.choerodon.issue.api.service.ProjectConfigService;
import io.choerodon.issue.api.service.ProjectInfoService;
import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.issue.fixdata.dto.StatusForMoveDataDO;
import io.choerodon.issue.fixdata.feign.FixStateMachineFeignClient;
import io.choerodon.issue.infra.enums.SchemeType;
import io.choerodon.issue.infra.feign.dto.StatusDTO;
import io.choerodon.issue.infra.mapper.StateMachineSchemeMapper;
import io.choerodon.issue.infra.utils.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private FixStateMachineFeignClient fixStateMachineFeignClient;
    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private StateMachineSchemeMapper stateMachineSchemeMapper;

    @Override
    public Map<Long, List<StatusDTO>> fixStateMachineScheme(List<StatusForMoveDataDO> statuses) {

        //创建状态
        Map<Long, List<StatusDTO>> statusOrgMap = fixStateMachineFeignClient.createStatus(statuses).getBody();

        //根据组织id分组
        Map<Long, List<StatusForMoveDataDO>> orgStatusMap = statuses.stream().collect(Collectors.groupingBy(StatusForMoveDataDO::getOrganizationId));
        for (Map.Entry<Long, List<StatusForMoveDataDO>> statusDOs : orgStatusMap.entrySet()) {
            Long organizationId = statusDOs.getKey();
            //根据项目id分组
            Map<Long, List<StatusForMoveDataDO>> proStatusMap = statusDOs.getValue().stream().collect(Collectors.groupingBy(StatusForMoveDataDO::getProjectId));
            for (Map.Entry<Long, List<StatusForMoveDataDO>> listEntry : proStatusMap.entrySet()) {
                Long projectId = listEntry.getKey();
                String projectCode = projectUtil.getCode(projectId);
                List<String> statusNames = listEntry.getValue().stream().map(StatusForMoveDataDO::getName).collect(Collectors.toList());
                //创建状态机
                Long stateMachineId = fixStateMachineFeignClient.createStateMachine(organizationId, projectCode, statusNames).getBody();
                //创建状态机方案
                StateMachineScheme scheme = new StateMachineScheme();
                scheme.setType(SchemeType.AGILE);
                scheme.setName(projectCode + "默认状态机方案");
                scheme.setDescription(projectCode + "默认状态机方案");
                scheme.setDefaultStateMachineId(stateMachineId);
                scheme.setOrganizationId(organizationId);
                int isInsert = stateMachineSchemeMapper.insert(scheme);
                if (isInsert != 1) {
                    throw new CommonException("error.stateMachineScheme.create");
                }
//                //创建项目信息及配置默认方案
//                projectInfoService.createProject(projectId, projectCode);
//                //关联默认方案
//                projectConfigService.create(projectId, scheme.getId(), null);
            }
        }
        return statusOrgMap;
    }
}
