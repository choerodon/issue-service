package io.choerodon.issue.infra.util;

import io.choerodon.issue.api.vo.StateMachineSchemeConfigVO;
import io.choerodon.issue.api.vo.StateMachineSchemeVO;
import io.choerodon.issue.infra.dto.ProjectConfig;
import io.choerodon.issue.infra.dto.StateMachineScheme;
import io.choerodon.issue.infra.dto.StateMachineSchemeConfig;
import io.choerodon.issue.infra.feign.vo.ProjectVO;
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


    public static StateMachineSchemeVO convertStateMachineSchemeToVO(final StateMachineScheme scheme, final Map<Long, ProjectVO> projectMap) {
        ModelMapper modelMapper = new ModelMapper();
        StateMachineSchemeVO schemeVO = modelMapper.map(scheme, StateMachineSchemeVO.class);
        List<StateMachineSchemeConfig> schemeConfigs = scheme.getSchemeConfigs();
        if (null != schemeConfigs && !schemeConfigs.isEmpty()) {
            List<StateMachineSchemeConfigVO> schemeConfigVOS = modelMapper.map(schemeConfigs, new TypeToken<List<StateMachineSchemeConfigVO>>() {
            }.getType());
            schemeVO.setConfigVOS(schemeConfigVOS);
        }
        List<ProjectConfig> projectConfigs = scheme.getProjectConfigs();
        if (null != projectConfigs && !projectConfigs.isEmpty()) {
            List<ProjectVO> projectVOS = new ArrayList<>(projectConfigs.size());
            for (ProjectConfig config : projectConfigs) {
                ProjectVO projectVO = projectMap.get(config.getProjectId());
                if (projectVO != null) {
                    projectVOS.add(projectVO);
                }
            }
            schemeVO.setProjectVOS(projectVOS);
        }
        return schemeVO;
    }

    public static List<StateMachineSchemeVO> convertStateMachineSchemesToVOS(final List<StateMachineScheme> schemes, final Map<Long, ProjectVO> projectMap) {
        List<StateMachineSchemeVO> list = new ArrayList<>(schemes.size());
        for (StateMachineScheme scheme : schemes) {
            StateMachineSchemeVO schemeVO = convertStateMachineSchemeToVO(scheme, projectMap);
            list.add(schemeVO);
        }
        return list;
    }

}
