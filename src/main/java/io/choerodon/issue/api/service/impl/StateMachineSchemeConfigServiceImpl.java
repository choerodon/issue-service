package io.choerodon.issue.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.StateMachineSchemeConfigDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.api.service.StateMachineSchemeConfigService;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.issue.domain.StateMachineSchemeConfig;
import io.choerodon.issue.domain.StateMachineSchemeConfigDraft;
import io.choerodon.issue.infra.enums.StateMachineSchemeStatus;
import io.choerodon.issue.infra.mapper.StateMachineSchemeConfigDraftMapper;
import io.choerodon.issue.infra.mapper.StateMachineSchemeConfigMapper;
import io.choerodon.issue.infra.mapper.StateMachineSchemeMapper;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @Date 2018/8/2
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class StateMachineSchemeConfigServiceImpl extends BaseServiceImpl<StateMachineSchemeConfigDraft> implements StateMachineSchemeConfigService {

    @Autowired
    private StateMachineSchemeConfigMapper configMapper;
    @Autowired
    private StateMachineSchemeConfigDraftMapper configDraftMapper;
    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;
    @Autowired
    private StateMachineSchemeMapper schemeMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public StateMachineSchemeDTO delete(Long organizationId, Long schemeId, Long stateMachineId) {
        //删除草稿
        StateMachineSchemeConfigDraft config = new StateMachineSchemeConfigDraft();
        config.setOrganizationId(organizationId);
        config.setSchemeId(schemeId);
        config.setStateMachineId(stateMachineId);
        int isDelete = configDraftMapper.delete(config);
        if (isDelete < 1) {
            throw new CommonException("error.stateMachineSchemeConfig.delete");
        }
        return stateMachineSchemeService.querySchemeWithConfigById(true, organizationId, schemeId);
    }

    @Override
    public void deleteBySchemeId(Long organizationId, Long schemeId) {
        //删除草稿
        StateMachineSchemeConfigDraft draft = new StateMachineSchemeConfigDraft();
        draft.setOrganizationId(organizationId);
        draft.setSchemeId(schemeId);
        configDraftMapper.delete(draft);
        //删除发布
        StateMachineSchemeConfig config = new StateMachineSchemeConfig();
        config.setOrganizationId(organizationId);
        config.setSchemeId(schemeId);
        configMapper.delete(config);
    }

    @Override
    public StateMachineSchemeDTO create(Long organizationId, Long schemeId, Long stateMachineId, List<StateMachineSchemeConfigDTO> schemeDTOs) {
        List<StateMachineSchemeConfigDraft> configs = modelMapper.map(schemeDTOs, new TypeToken<List<StateMachineSchemeConfigDraft>>() {
        }.getType());
        //删除之前的草稿配置
        StateMachineSchemeConfigDraft delConfig = new StateMachineSchemeConfigDraft();
        delConfig.setSchemeId(schemeId);
        delConfig.setStateMachineId(stateMachineId);
        delConfig.setDefault(false);
        configDraftMapper.delete(delConfig);
        for (StateMachineSchemeConfigDraft config : configs) {
            delConfig.setStateMachineId(null);
            delConfig.setSchemeId(schemeId);
            delConfig.setIssueTypeId(config.getIssueTypeId());
            configDraftMapper.delete(delConfig);
            config.setSchemeId(schemeId);
            config.setStateMachineId(stateMachineId);
            config.setOrganizationId(organizationId);
            config.setDefault(false);
        }
        configs.forEach(c -> configDraftMapper.insert(c));
        return stateMachineSchemeService.querySchemeWithConfigById(true, organizationId, schemeId);
    }

    @Override
    public void createDefaultConfig(Long organizationId, Long schemeId, Long stateMachineId) {
        //创建草稿
        StateMachineSchemeConfigDraft defaultConfig = new StateMachineSchemeConfigDraft();
        defaultConfig.setStateMachineId(stateMachineId);
        defaultConfig.setSequence(0);
        defaultConfig.setIssueTypeId(0L);
        defaultConfig.setSchemeId(schemeId);
        defaultConfig.setOrganizationId(organizationId);
        defaultConfig.setDefault(true);
        int isInsert = configDraftMapper.insert(defaultConfig);
        if (isInsert < 1) {
            throw new CommonException("error.stateMachineSchemeConfig.insert");
        }
    }

    @Override
    public void updateDefaultConfig(Long organizationId, Long schemeId, Long stateMachineId) {
        //更新草稿
        StateMachineSchemeConfigDraft defaultConfig = configDraftMapper.selectDefault(organizationId, schemeId);
        defaultConfig.setStateMachineId(stateMachineId);
        updateOptional(defaultConfig, "stateMachineId");
    }

    @Override
    public StateMachineSchemeConfigDTO selectDefault(Boolean isDraft, Long organizationId, Long schemeId) {
        StateMachineSchemeConfigDTO configDTO;
        if (isDraft) {
            configDTO = modelMapper.map(configDraftMapper.selectDefault(organizationId, schemeId), StateMachineSchemeConfigDTO.class);
        } else {
            configDTO = modelMapper.map(configMapper.selectDefault(organizationId, schemeId), StateMachineSchemeConfigDTO.class);
        }
        return configDTO;
    }

    @Override
    public Long queryStateMachineIdBySchemeIdAndIssueTypeId(Boolean isDraft, Long organizationId, Long schemeId, Long issueTypeId) {
        if (isDraft) {
            StateMachineSchemeConfigDraft config = new StateMachineSchemeConfigDraft();
            config.setOrganizationId(organizationId);
            config.setSchemeId(schemeId);
            config.setIssueTypeId(issueTypeId);
            List<StateMachineSchemeConfigDraft> configs = configDraftMapper.select(config);
            if (!configs.isEmpty()) {
                return configs.get(0).getStateMachineId();
            } else {
                //找不到对应的issueType则取默认
                return configDraftMapper.selectDefault(organizationId, schemeId).getStateMachineId();
            }
        } else {
            StateMachineSchemeConfig config = new StateMachineSchemeConfig();
            config.setOrganizationId(organizationId);
            config.setSchemeId(schemeId);
            config.setIssueTypeId(issueTypeId);
            List<StateMachineSchemeConfig> configs = configMapper.select(config);
            if (!configs.isEmpty()) {
                return configs.get(0).getStateMachineId();
            } else {
                //找不到对应的issueType则取默认
                return configMapper.selectDefault(organizationId, schemeId).getStateMachineId();
            }
        }
    }

    @Override
    public List<Long> queryIssueTypeIdBySchemeIdAndStateMachineId(Boolean isDraft, Long organizationId, Long schemeId, Long stateMachineId) {
        if (isDraft) {
            StateMachineSchemeConfigDraft config = new StateMachineSchemeConfigDraft();
            config.setOrganizationId(organizationId);
            config.setSchemeId(schemeId);
            config.setStateMachineId(stateMachineId);
            List<StateMachineSchemeConfigDraft> configs = configDraftMapper.select(config);
            return configs.stream().map(StateMachineSchemeConfigDraft::getIssueTypeId).collect(Collectors.toList());
        } else {
            StateMachineSchemeConfig config = new StateMachineSchemeConfig();
            config.setOrganizationId(organizationId);
            config.setSchemeId(schemeId);
            config.setStateMachineId(stateMachineId);
            List<StateMachineSchemeConfig> configs = configMapper.select(config);
            return configs.stream().map(StateMachineSchemeConfig::getIssueTypeId).collect(Collectors.toList());
        }
    }

    @Override
    public List<StateMachineSchemeConfigDTO> queryBySchemeId(Boolean isDraft, Long organizationId, Long schemeId) {
        List<StateMachineSchemeConfigDTO> configDTOs;
        if (isDraft) {
            StateMachineSchemeConfigDraft select = new StateMachineSchemeConfigDraft();
            select.setOrganizationId(organizationId);
            select.setSchemeId(schemeId);
            configDTOs = modelMapper.map(configDraftMapper.select(select), new TypeToken<List<StateMachineSchemeConfigDTO>>() {
            }.getType());
        } else {
            StateMachineSchemeConfig select = new StateMachineSchemeConfig();
            select.setOrganizationId(organizationId);
            select.setSchemeId(schemeId);
            configDTOs = modelMapper.map(configMapper.select(select), new TypeToken<List<StateMachineSchemeConfigDTO>>() {
            }.getType());
        }
        return configDTOs;
    }

    @Override
    public List<Long> querySchemeIdsByStateMachineId(Boolean isDraft, Long organizationId, Long stateMachineId) {
        List<Long> schemeIds;
        if (isDraft) {
            StateMachineSchemeConfigDraft select = new StateMachineSchemeConfigDraft();
            select.setStateMachineId(stateMachineId);
            select.setOrganizationId(organizationId);
            schemeIds = configDraftMapper.select(select).stream().map(StateMachineSchemeConfigDraft::getSchemeId).distinct().collect(Collectors.toList());
        } else {
            StateMachineSchemeConfig select = new StateMachineSchemeConfig();
            select.setStateMachineId(stateMachineId);
            select.setOrganizationId(organizationId);
            schemeIds = configMapper.select(select).stream().map(StateMachineSchemeConfig::getSchemeId).distinct().collect(Collectors.toList());
        }
        return schemeIds;
    }

    @Override
    public StateMachineSchemeDTO deploy(Long organizationId, Long schemeId) {
        return null;
    }

    @Override
    public StateMachineSchemeDTO checkDeploy(Long organizationId, Long schemeId) {
        //获取发布配置
        StateMachineSchemeConfig config = new StateMachineSchemeConfig();
        config.setSchemeId(schemeId);
        config.setOrganizationId(organizationId);
        List<StateMachineSchemeConfig> deploys = configMapper.select(config);
        Map<Long, Long> deployMap = deploys.stream().collect(Collectors.toMap(StateMachineSchemeConfig::getIssueTypeId, StateMachineSchemeConfig::getStateMachineId));
        Long deployDefaultStateMachineId = deployMap.get(0L);
        deployMap.remove(0L);
        //获取草稿配置
        StateMachineSchemeConfigDraft draft = new StateMachineSchemeConfigDraft();
        draft.setSchemeId(schemeId);
        draft.setOrganizationId(organizationId);
        List<StateMachineSchemeConfigDraft> drafts = configDraftMapper.select(draft);
        Map<Long, Long> draftMap = drafts.stream().collect(Collectors.toMap(StateMachineSchemeConfigDraft::getIssueTypeId, StateMachineSchemeConfigDraft::getStateMachineId));
        Long draftDefaultStateMachineId = draftMap.get(0L);
        draftMap.remove(0L);
        //判断状态机有变化的问题类型
        int size = deployMap.size()+draftMap.size();
        Map<Long, Long> oldMap = new HashMap<>(size);
        Map<Long, Long> newMap = new HashMap<>(size);
        //因为发布的和草稿的都可能有增加或减少，因此需要两边都判断
        for (Map.Entry<Long, Long> entry : deployMap.entrySet()) {
            Long issueTypeId = entry.getKey();
            Long oldStateMachineId = entry.getValue();
            Long newStateMachineId = draftMap.getOrDefault(issueTypeId, draftDefaultStateMachineId);
            if (!oldStateMachineId.equals(newStateMachineId)) {
                oldMap.put(issueTypeId, oldStateMachineId);
                newMap.put(issueTypeId, newStateMachineId);
            }
        }
        for (Map.Entry<Long, Long> entry : draftMap.entrySet()) {
            Long issueTypeId = entry.getKey();
            //未判断过
            if(oldMap.get(issueTypeId)==null){
                Long oldStateMachineId = entry.getValue();
                Long newStateMachineId = deployMap.getOrDefault(issueTypeId, deployDefaultStateMachineId);
                if (!oldStateMachineId.equals(newStateMachineId)) {
                    oldMap.put(issueTypeId, oldStateMachineId);
                    newMap.put(issueTypeId, newStateMachineId);
                }
            }
        }
        return null;
    }

    @Override
    public StateMachineSchemeDTO deleteDraft(Long organizationId, Long schemeId) {
        //删除草稿配置
        StateMachineSchemeConfigDraft draft = new StateMachineSchemeConfigDraft();
        draft.setSchemeId(schemeId);
        draft.setOrganizationId(organizationId);
        configDraftMapper.delete(draft);
        //写入活跃的配置写到到草稿中，id一致
        StateMachineSchemeConfig config = new StateMachineSchemeConfig();
        config.setSchemeId(schemeId);
        config.setOrganizationId(organizationId);
        List<StateMachineSchemeConfig> configs = configMapper.select(config);
        if (configs != null && !configs.isEmpty()) {
            List<StateMachineSchemeConfigDraft> configDrafts = modelMapper.map(configs, new TypeToken<List<StateMachineSchemeConfigDraft>>() {
            }.getType());
            for (StateMachineSchemeConfigDraft insertConfig : configDrafts) {
                int result = configDraftMapper.insert(insertConfig);
                if (result != 1) {
                    throw new CommonException("error.stateMachineSchemeConfig.create");
                }
            }
        }
        //更新状态机方案状态为：活跃
        StateMachineScheme scheme = schemeMapper.selectByPrimaryKey(schemeId);
        scheme.setStatus(StateMachineSchemeStatus.ACTIVE);
        return stateMachineSchemeService.querySchemeWithConfigById(false, organizationId, schemeId);
    }
}
