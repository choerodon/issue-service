package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.StateMachineSchemeConfigDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.api.service.StateMachineSchemeConfigService;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.domain.StateMachineSchemeConfig;
import io.choerodon.issue.infra.mapper.StateMachineSchemeConfigMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author shinan.chen
 * @Date 2018/8/2
 */
@Component
public class StateMachineSchemeConfigServiceImpl extends BaseServiceImpl<StateMachineSchemeConfig> implements StateMachineSchemeConfigService {

    @Autowired
    private StateMachineSchemeConfigMapper configMapper;

    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public StateMachineSchemeDTO delete(Long organizationId, Long schemeId, Long stateMachineId) {
        StateMachineSchemeConfig config = new StateMachineSchemeConfig();
        config.setStateMachineId(stateMachineId);
        int isDelete = configMapper.delete(config);
        if (isDelete < 1) {
            throw new CommonException("error.stateMachineSchemeConfig.delete");
        }
        return stateMachineSchemeService.getSchemeWithConfigById(organizationId, schemeId);
    }

    @Override
    @Transactional
    public StateMachineSchemeDTO create(Long organizationId, Long schemeId, Long stateMachineId, List<StateMachineSchemeConfigDTO> schemeDTOs) {
        List<StateMachineSchemeConfig> configs = modelMapper.map(schemeDTOs, new TypeToken<List<StateMachineSchemeConfig>>() {}.getType());
        //删除之前的配置
        StateMachineSchemeConfig delConfig = new StateMachineSchemeConfig();
        delConfig.setSchemeId(schemeId);
        delConfig.setStateMachineId(stateMachineId);
        configMapper.delete(delConfig);
        for (StateMachineSchemeConfig config:configs) {
            delConfig.setStateMachineId(null);
            delConfig.setSchemeId(schemeId);
            delConfig.setIssueTypeId(config.getIssueTypeId());
            configMapper.delete(delConfig);
            config.setSchemeId(schemeId);
            config.setStateMachineId(stateMachineId);
        }
        int isInsert = configMapper.insertList(configs);
        if (isInsert < 1) {
            throw new CommonException("error.stateMachineSchemeConfig.insert");
        }
        return stateMachineSchemeService.getSchemeWithConfigById(organizationId, schemeId);
    }


}
