package io.choerodon.issue.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeConfigDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeConfigViewDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.domain.IssueType;
import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.issue.domain.StateMachineSchemeConfig;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.mapper.IssueTypeMapper;
import io.choerodon.issue.infra.mapper.StateMachineSchemeConfigMapper;
import io.choerodon.issue.infra.mapper.StateMachineSchemeMapper;
import io.choerodon.issue.infra.utils.ConvertUtils;
import io.choerodon.issue.infra.utils.ProjectUtil;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author shinan.chen
 * @Date 2018/8/2
 */
@Component
public class StateMachineSchemeServiceImpl extends BaseServiceImpl<StateMachineScheme> implements StateMachineSchemeService {

    @Autowired
    private StateMachineSchemeMapper schemeMapper;

    @Autowired
    private StateMachineSchemeConfigMapper configMapper;

    @Autowired
    private IssueTypeMapper issueTypeMapper;

    @Autowired
    private StateMachineFeignClient stateMachineServiceFeign;
    @Autowired
    private ProjectUtil projectUtil;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public Page<StateMachineSchemeDTO> pageQuery(PageRequest pageRequest, StateMachineSchemeDTO schemeDTO, String params) {
        StateMachineScheme scheme = modelMapper.map(schemeDTO, StateMachineScheme.class);
        Page<StateMachineScheme> page = PageHelper.doPageAndSort(pageRequest,
                () -> schemeMapper.fulltextSearch(scheme, params));
        List<StateMachineScheme> schemes = page.getContent();
        List<StateMachineSchemeDTO> schemeDTOS = ConvertUtils.convertStateMachineSchemesToDTOs(schemes);
        if (schemeDTOS != null) {
            for (StateMachineSchemeDTO machineSchemeDTO : schemeDTOS) {
                if (machineSchemeDTO.getConfigDTOs() != null) {
                    for (StateMachineSchemeConfigDTO configDTO : machineSchemeDTO.getConfigDTOs()) {
                        IssueType issueType = issueTypeMapper.selectByPrimaryKey(configDTO.getIssueTypeId());
                        if (issueType != null) {
                            configDTO.setIssueTypeName(issueType.getName());
                            configDTO.setIssueTypeIcon(issueType.getIcon());
                        }
                        StateMachineDTO stateMachineDTO = stateMachineServiceFeign.queryStateMachineById(schemeDTO.getOrganizationId(), configDTO.getStateMachineId()).getBody();
                        configDTO.setStateMachineName(stateMachineDTO.getName());
                    }
                }
            }
        }

        Page<StateMachineSchemeDTO> returnPage = new Page<>();
        returnPage.setContent(schemeDTOS);
        returnPage.setNumber(page.getNumber());
        returnPage.setNumberOfElements(page.getNumberOfElements());
        returnPage.setSize(page.getSize());
        returnPage.setTotalElements(page.getTotalElements());
        returnPage.setTotalPages(page.getTotalPages());
        return returnPage;
    }

    @Override
    public StateMachineSchemeDTO create(Long organizationId, StateMachineSchemeDTO schemeDTO) {
        StateMachineScheme scheme = modelMapper.map(schemeDTO, StateMachineScheme.class);
        scheme.setOrganizationId(organizationId);
        int isInsert = schemeMapper.insert(scheme);
        if (isInsert != 1) {
            throw new CommonException("error.stateMachineScheme.create");
        }
        scheme = schemeMapper.selectByPrimaryKey(scheme);
        return modelMapper.map(scheme, StateMachineSchemeDTO.class);
    }

    @Override
    public StateMachineSchemeDTO update(Long organizationId, Long schemeId, StateMachineSchemeDTO schemeDTO) {
        schemeDTO.setId(schemeId);
        schemeDTO.setOrganizationId(organizationId);
        StateMachineScheme scheme = modelMapper.map(schemeDTO, StateMachineScheme.class);
        int isUpdate = schemeMapper.updateByPrimaryKeySelective(scheme);
        if (isUpdate != 1) {
            throw new CommonException("error.stateMachineScheme.update");
        }
        scheme = schemeMapper.selectByPrimaryKey(scheme);
        return modelMapper.map(scheme, StateMachineSchemeDTO.class);
    }

    @Override
    @Transactional(rollbackFor = CommonException.class)
    public Boolean delete(Long organizationId, Long schemeId) {
        if (schemeId == null) {
            throw new CommonException("error.stateMachineScheme.delete.schemeId.null");
        }
        int isDelete = schemeMapper.deleteByPrimaryKey(schemeId);
        if (isDelete != 1) {
            throw new CommonException("error.stateMachineScheme.delete");
        }
        //删除方案配置信息
        StateMachineSchemeConfig config = new StateMachineSchemeConfig();
        config.setSchemeId(schemeId);
        configMapper.delete(config);
        return true;
    }

    @Override
    public StateMachineSchemeDTO getSchemeWithConfigById(Long organizationId, Long schemeId) {
        StateMachineScheme scheme = schemeMapper.getSchemeWithConfigById(schemeId);
        if (scheme == null) {
            throw new CommonException("error.stateMachineScheme.getSchemeWithConfigById.notFound");
        }
        StateMachineSchemeDTO schemeDTO = modelMapper.map(scheme, StateMachineSchemeDTO.class);

        //处理配置信息
        List<StateMachineSchemeConfig> configs = scheme.getSchemeConfigs();
        Map<Long, List<IssueType>> map = new HashMap<>();
        for (StateMachineSchemeConfig config : configs) {
            List<IssueType> issueTypes = map.get(config.getStateMachineId());
            if (issueTypes == null) {
                issueTypes = new ArrayList<>();
            }
            IssueType issueType = issueTypeMapper.selectByPrimaryKey(config.getIssueTypeId());
            issueTypes.add(issueType);
            map.put(config.getStateMachineId(), issueTypes);
        }

        List<StateMachineSchemeConfigViewDTO> viewDTOs = new ArrayList<>();
        for (Map.Entry<Long, List<IssueType>> entry : map.entrySet()) {
            Long stateMachineId = entry.getKey();
            List<IssueType> issueTypes = entry.getValue();
            StateMachineDTO stateMachineDTO = stateMachineServiceFeign.queryStateMachineById(organizationId, stateMachineId).getBody();
            StateMachineSchemeConfigViewDTO viewDTO = new StateMachineSchemeConfigViewDTO();
            viewDTO.setStateMachineDTO(stateMachineDTO);

            List<IssueTypeDTO> issueTypeDTOs = modelMapper.map(issueTypes, new TypeToken<List<IssueTypeDTO>>() {
            }.getType());
            viewDTO.setIssueTypeDTOs(issueTypeDTOs);
            viewDTOs.add(viewDTO);
        }
        schemeDTO.setViewDTOs(viewDTOs);
        return schemeDTO;
    }

    @Override
    @Transactional(rollbackFor = CommonException.class)
    public List<StateMachineSchemeConfigDTO> createSchemeConfig(Long organizationId, Long schemeId, List<StateMachineSchemeConfigDTO> configDTOs) {
        List<StateMachineSchemeConfig> configs = modelMapper.map(configDTOs, new TypeToken<List<StateMachineSchemeConfig>>() {
        }.getType());
        for (StateMachineSchemeConfig config : configs) {
            config.setSchemeId(schemeId);
        }
        int isInsert = configMapper.insertList(configs);
        if (isInsert < 1) {
            throw new CommonException("error.StateMachineSchemeConfig.insert");
        }
        return modelMapper.map(configs, new TypeToken<List<StateMachineSchemeConfigDTO>>() {
        }.getType());
    }

    @Override
    public Boolean checkName(Long organizationId, Long schemeId, String name) {
        StateMachineScheme scheme = new StateMachineScheme();
        scheme.setOrganizationId(organizationId);
        scheme.setName(name);
        scheme = schemeMapper.selectOne(scheme);
        if (scheme != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验
            return scheme.getId().equals(schemeId);
        }
        return true;
    }

    @Override
    public List<StateMachineSchemeDTO> querySchemeByStateMachineId(Long organizationId, Long stateMachineId) {
        List<StateMachineScheme> stateMachineSchemes = schemeMapper.querySchemeByStateMachineId(organizationId, stateMachineId);
        if (stateMachineSchemes != null && !stateMachineSchemes.isEmpty()) {
            return modelMapper.map(stateMachineSchemes, new TypeToken<List<StateMachineSchemeDTO>>() {
            }.getType());
        }
        return Collections.emptyList();
    }

    @Override
    public StateMachineScheme createSchemeWithCreateProject(Long projectId, String projectCode) {

        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long stateMachineId = stateMachineServiceFeign.createStateMachineWithCreateProject(organizationId, projectCode).getBody();

        StateMachineScheme scheme = new StateMachineScheme();
        scheme.setName(projectCode + "默认状态机方案");
        scheme.setDescription(projectCode + "默认状态机方案");
        scheme.setDefaultStateMachineId(stateMachineId);
        scheme.setOrganizationId(organizationId);
        int isInsert = schemeMapper.insert(scheme);
        if (isInsert != 1) {
            throw new CommonException("error.stateMachineScheme.create");
        }
        return scheme;
    }
}
