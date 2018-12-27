package io.choerodon.issue.infra.utils;

import io.choerodon.issue.api.dto.ProjectDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeConfigDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.issue.domain.StateMachineSchemeConfig;
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


    public static StateMachineSchemeDTO convertStateMachineSchemeToDTO(final StateMachineScheme scheme, final Map<Long, ProjectDTO> projectMap) {
        ModelMapper modelMapper = new ModelMapper();
        StateMachineSchemeDTO schemeDTO = modelMapper.map(scheme, StateMachineSchemeDTO.class);
        List<StateMachineSchemeConfig> schemeConfigs = scheme.getSchemeConfigs();
        if (null != schemeConfigs && !schemeConfigs.isEmpty()) {
            List<StateMachineSchemeConfigDTO> schemeConfigDTOS = modelMapper.map(schemeConfigs, new TypeToken<List<StateMachineSchemeConfigDTO>>() {
            }.getType());
            schemeDTO.setConfigDTOs(schemeConfigDTOS);
        }
        List<ProjectConfig> projectConfigs = scheme.getProjectConfigs();
        if (null != projectConfigs && !projectConfigs.isEmpty()) {
            List<ProjectDTO> projectDTOS = new ArrayList<>(projectConfigs.size());
            for (ProjectConfig config : projectConfigs) {
                ProjectDTO projectDTO = projectMap.get(config.getProjectId());
                if (projectDTO != null) {
                    projectDTOS.add(projectDTO);
                }
            }
            schemeDTO.setProjectDTOs(projectDTOS);
        }
        return schemeDTO;
    }

    public static List<StateMachineSchemeDTO> convertStateMachineSchemesToDTOs(final List<StateMachineScheme> schemes, final Map<Long, ProjectDTO> projectMap) {
        List<StateMachineSchemeDTO> list = new ArrayList<>(schemes.size());
        for (StateMachineScheme scheme : schemes) {
            StateMachineSchemeDTO schemeDTO = convertStateMachineSchemeToDTO(scheme, projectMap);
            list.add(schemeDTO);
        }
        return list;
    }

}
