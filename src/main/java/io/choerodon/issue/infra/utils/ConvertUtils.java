package io.choerodon.issue.infra.utils;

import io.choerodon.issue.api.vo.StateMachineSchemeConfigVO;
import io.choerodon.issue.api.vo.StateMachineSchemeVO;
import io.choerodon.issue.infra.dto.ProjectConfigDTO;
import io.choerodon.issue.infra.dto.StateMachineSchemeDTO;
import io.choerodon.issue.infra.dto.StateMachineSchemeConfigDTO;
import io.choerodon.issue.infra.feign.vo.ProjectDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author peng.jiang@hand-china.com
 */
public class ConvertUtils {
    private ConvertUtils() {
    }


    public static StateMachineSchemeVO convertStateMachineSchemeToVO(final StateMachineSchemeDTO scheme, final Map<Long, ProjectDTO> projectMap) {
        ModelMapper modelMapper = new ModelMapper();
        StateMachineSchemeVO schemeVO = modelMapper.map(scheme, StateMachineSchemeVO.class);
        List<StateMachineSchemeConfigDTO> schemeConfigs = scheme.getSchemeConfigs();
        if (null != schemeConfigs && !schemeConfigs.isEmpty()) {
            List<StateMachineSchemeConfigVO> schemeConfigVOS = modelMapper.map(schemeConfigs, new TypeToken<List<StateMachineSchemeConfigVO>>() {
            }.getType());
            schemeVO.setConfigVOS(schemeConfigVOS);
        }
        List<ProjectConfigDTO> projectConfigs = scheme.getProjectConfigs();
        if (null != projectConfigs && !projectConfigs.isEmpty()) {
            List<ProjectDTO> projectVOS = new ArrayList<>(projectConfigs.size());
            for (ProjectConfigDTO config : projectConfigs) {
                ProjectDTO projectVO = projectMap.get(config.getProjectId());
                if (projectVO != null) {
                    projectVOS.add(projectVO);
                }
            }
            schemeVO.setProjectVOS(projectVOS);
        }
        return schemeVO;
    }

    public static List<StateMachineSchemeVO> convertStateMachineSchemesToVOS(final List<StateMachineSchemeDTO> schemes, final Map<Long, ProjectDTO> projectMap) {
        List<StateMachineSchemeVO> list = new ArrayList<>(schemes.size());
        for (StateMachineSchemeDTO scheme : schemes) {
            StateMachineSchemeVO schemeVO = convertStateMachineSchemeToVO(scheme, projectMap);
            list.add(schemeVO);
        }
        return list;
    }

}
