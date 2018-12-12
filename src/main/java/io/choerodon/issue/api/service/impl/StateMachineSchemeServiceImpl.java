package io.choerodon.issue.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.*;
import io.choerodon.issue.api.dto.payload.ProjectEvent;
import io.choerodon.issue.api.service.ProjectConfigService;
import io.choerodon.issue.api.service.StateMachineSchemeConfigService;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.domain.IssueType;
import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.issue.infra.enums.SchemeApplyType;
import io.choerodon.issue.infra.enums.SchemeType;
import io.choerodon.issue.infra.enums.StateMachineSchemeStatus;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.UserFeignClient;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.mapper.IssueTypeMapper;
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
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @Date 2018/8/2
 */
@Component
public class StateMachineSchemeServiceImpl extends BaseServiceImpl<StateMachineScheme> implements StateMachineSchemeService {

    private static final String WITHOUT_CONFIG_ISSUE_TYPE_NAME = "未分配类型";
    private static final String WITHOUT_CONFIG_ISSUE_TYPE_ICON = "style";
    private static final String WITHOUT_CONFIG_ISSUE_TYPE_COLOUR = "#808080";
    @Autowired
    private StateMachineSchemeMapper schemeMapper;
    @Autowired
    private StateMachineSchemeConfigService configService;
    @Autowired
    private IssueTypeMapper issueTypeMapper;
    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;
    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private UserFeignClient userFeignClient;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public Page<StateMachineSchemeDTO> pageQuery(Long organizationId, PageRequest pageRequest, StateMachineSchemeDTO schemeDTO, String params) {
        //查询出组织下的所有项目
        List<ProjectDTO> projectDTOs = userFeignClient.queryProjectsByOrgId(organizationId, 0, 999, new String[]{}, null, null, null, new String[]{}).getBody().getContent();
        Map<Long, ProjectDTO> projectMap = projectDTOs.stream().collect(Collectors.toMap(ProjectDTO::getId, x -> x));
        //查询组织下的所有问题类型
        List<IssueType> issueTypes = issueTypeMapper.queryByOrgId(organizationId);
        Map<Long, IssueType> issueTypeMap = issueTypes.stream().collect(Collectors.toMap(IssueType::getId, x -> x));
        //查询组织下的所有状态机
        List<StateMachineDTO> stateMachineDTOS = stateMachineFeignClient.queryByOrgId(organizationId).getBody();
        Map<Long, StateMachineDTO> stateMachineDTOMap = stateMachineDTOS.stream().collect(Collectors.toMap(StateMachineDTO::getId, x -> x));

        StateMachineScheme scheme = modelMapper.map(schemeDTO, StateMachineScheme.class);
        Page<StateMachineScheme> page = PageHelper.doPageAndSort(pageRequest,
                () -> schemeMapper.fulltextSearch(scheme, params));

        List<StateMachineScheme> schemes = page.getContent();
        List<StateMachineScheme> schemesWithConfig = schemeMapper.queryByIdsWithConfig(organizationId, schemes.stream().map(StateMachineScheme::getId).collect(Collectors.toList()));
        List<StateMachineSchemeDTO> schemeDTOS = ConvertUtils.convertStateMachineSchemesToDTOs(schemesWithConfig, projectMap);
        if (schemeDTOS != null) {
            for (StateMachineSchemeDTO machineSchemeDTO : schemeDTOS) {
                if (machineSchemeDTO.getConfigDTOs() != null) {
                    for (StateMachineSchemeConfigDTO configDTO : machineSchemeDTO.getConfigDTOs()) {
                        if (!configDTO.getDefault()) {
                            IssueType issueType = issueTypeMap.get(configDTO.getIssueTypeId());
                            if (issueType != null) {
                                configDTO.setIssueTypeName(issueType.getName());
                                configDTO.setIssueTypeIcon(issueType.getIcon());
                                configDTO.setIssueTypeColour(issueType.getColour());
                            }
                        } else {
                            //若为默认配置，则匹配的是所有为分配的问题类型
                            configDTO.setIssueTypeName(WITHOUT_CONFIG_ISSUE_TYPE_NAME);
                            configDTO.setIssueTypeIcon(WITHOUT_CONFIG_ISSUE_TYPE_ICON);
                            configDTO.setIssueTypeColour(WITHOUT_CONFIG_ISSUE_TYPE_COLOUR);
                        }
                        StateMachineDTO stateMachineDTO = stateMachineDTOMap.get(configDTO.getStateMachineId());
                        if (stateMachineDTO != null) {
                            configDTO.setStateMachineName(stateMachineDTO.getName());
                        }
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
        if (checkName(organizationId, schemeDTO.getName())) {
            throw new CommonException("error.stateMachineName.exist");
        }
        schemeDTO.setStatus(StateMachineSchemeStatus.CREATE);
        StateMachineScheme scheme = modelMapper.map(schemeDTO, StateMachineScheme.class);
        scheme.setOrganizationId(organizationId);
        int isInsert = schemeMapper.insert(scheme);
        if (isInsert != 1) {
            throw new CommonException("error.stateMachineScheme.create");
        }

        //创建一个defaultConfig
        StateMachineDTO stateMachineDTO = stateMachineFeignClient.queryDefaultStateMachine(organizationId).getBody();
        configService.createDefaultConfig(organizationId, scheme.getId(), stateMachineDTO.getId());

        scheme = schemeMapper.selectByPrimaryKey(scheme);
        return modelMapper.map(scheme, StateMachineSchemeDTO.class);
    }

    private Boolean checkNameUpdate(Long organizationId, Long schemeId, String name) {
        StateMachineScheme scheme = new StateMachineScheme();
        scheme.setOrganizationId(organizationId);
        scheme.setName(name);
        StateMachineScheme res = schemeMapper.selectOne(scheme);
        if (res != null && !schemeId.equals(res.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public StateMachineSchemeDTO update(Long organizationId, Long schemeId, StateMachineSchemeDTO schemeDTO) {
        if (checkNameUpdate(organizationId, schemeId, schemeDTO.getName())) {
            throw new CommonException("error.stateMachineName.exist");
        }
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
        StateMachineScheme scheme = schemeMapper.selectByPrimaryKey(schemeId);
        if (!scheme.getStatus().equals(StateMachineSchemeStatus.CREATE)) {
            throw new CommonException("error.stateMachineScheme.delete.illegal");
        }
        if (schemeId == null) {
            throw new CommonException("error.stateMachineScheme.delete.schemeId.null");
        }
        int isDelete = schemeMapper.deleteByPrimaryKey(schemeId);
        if (isDelete != 1) {
            throw new CommonException("error.stateMachineScheme.delete");
        }
        //删除方案配置信息
        configService.deleteBySchemeId(organizationId, schemeId);
        return true;
    }

    @Override
    public StateMachineSchemeDTO querySchemeWithConfigById(Boolean isDraft, Long organizationId, Long schemeId) {
        StateMachineScheme scheme = schemeMapper.selectByPrimaryKey(schemeId);
        if (scheme == null) {
            throw new CommonException("error.stateMachineScheme.notFound");
        }
        StateMachineSchemeDTO schemeDTO = modelMapper.map(scheme, StateMachineSchemeDTO.class);
        //处理配置信息
        List<StateMachineSchemeConfigDTO> configs = configService.queryBySchemeId(isDraft, organizationId, schemeId);
        Map<Long, List<IssueType>> map = new HashMap<>(configs.size());
        //取默认配置到第一个
        Long defaultStateMachineId = null;
        for (StateMachineSchemeConfigDTO config : configs) {
            List<IssueType> issueTypes = map.get(config.getStateMachineId());
            if (issueTypes == null) {
                issueTypes = new ArrayList<>();
            }
            IssueType issueType;
            if (!config.getDefault()) {
                issueType = issueTypeMapper.selectByPrimaryKey(config.getIssueTypeId());
            } else {
                //若为默认配置，则匹配的是所有为分配的问题类型
                issueType = new IssueType();
                issueType.setName(WITHOUT_CONFIG_ISSUE_TYPE_NAME);
                issueType.setIcon(WITHOUT_CONFIG_ISSUE_TYPE_ICON);
                issueType.setColour(WITHOUT_CONFIG_ISSUE_TYPE_COLOUR);
                defaultStateMachineId = config.getStateMachineId();
            }
            issueTypes.add(issueType);
            map.put(config.getStateMachineId(), issueTypes);
        }

        List<StateMachineSchemeConfigViewDTO> viewDTOs = new ArrayList<>();
        //处理默认配置
        viewDTOs.add(handleDefaultConfig(organizationId, defaultStateMachineId, map));
        for (Map.Entry<Long, List<IssueType>> entry : map.entrySet()) {
            Long stateMachineId = entry.getKey();
            List<IssueType> issueTypes = entry.getValue();
            StateMachineDTO stateMachineDTO = stateMachineFeignClient.queryStateMachineById(organizationId, stateMachineId).getBody();
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

    /**
     * 处理默认配置到首位
     *
     * @param organizationId
     * @param defaultStateMachineId
     * @param map
     * @return
     */
    private StateMachineSchemeConfigViewDTO handleDefaultConfig(Long organizationId, Long defaultStateMachineId, Map<Long, List<IssueType>> map) {
        StateMachineSchemeConfigViewDTO firstDTO = new StateMachineSchemeConfigViewDTO();
        StateMachineDTO stateMachineDTO = stateMachineFeignClient.queryStateMachineById(organizationId, defaultStateMachineId).getBody();
        firstDTO.setStateMachineDTO(stateMachineDTO);
        firstDTO.setIssueTypeDTOs(modelMapper.map(map.get(defaultStateMachineId), new TypeToken<ArrayList<IssueTypeDTO>>() {
        }.getType()));
        map.remove(defaultStateMachineId);
        return firstDTO;
    }

    @Override
    public Boolean checkName(Long organizationId, String name) {
        StateMachineScheme scheme = new StateMachineScheme();
        scheme.setOrganizationId(organizationId);
        scheme.setName(name);
        StateMachineScheme res = schemeMapper.selectOne(scheme);
        if (res == null) {
            return false;
        }
        return true;
    }

    @Override
    public List<StateMachineSchemeDTO> querySchemeByStateMachineId(Long organizationId, Long stateMachineId) {
        List<Long> deploySchemeIds = configService.querySchemeIdsByStateMachineId(false, organizationId, stateMachineId);
        List<Long> draftSchemeIds = configService.querySchemeIdsByStateMachineId(true, organizationId, stateMachineId);
        deploySchemeIds.addAll(draftSchemeIds);
        deploySchemeIds = deploySchemeIds.stream().distinct().collect(Collectors.toList());
        if (!deploySchemeIds.isEmpty()) {
            List<StateMachineScheme> stateMachineSchemes = schemeMapper.queryByIds(organizationId, deploySchemeIds);
            if (stateMachineSchemes != null && !stateMachineSchemes.isEmpty()) {
                return modelMapper.map(stateMachineSchemes, new TypeToken<List<StateMachineSchemeDTO>>() {
                }.getType());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void initByConsumeCreateProject(ProjectEvent projectEvent) {
        String projectCode = projectEvent.getProjectCode();
        //创建敏捷状态机方案
        initScheme(projectCode + "默认状态机方案【敏捷】", SchemeApplyType.AGILE, projectEvent);
        //创建测试状态机方案
        initScheme(projectCode + "默认状态机方案【测试】", SchemeApplyType.TEST, projectEvent);
    }

    /**
     * 初始化状态机方案
     *
     * @param name
     * @param schemeApplyType
     * @param projectEvent
     */
    private void initScheme(String name, String schemeApplyType, ProjectEvent projectEvent) {
        Long projectId = projectEvent.getProjectId();
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long stateMachineId = stateMachineFeignClient.createStateMachineWithCreateProject(organizationId, schemeApplyType, projectEvent).getBody();

        StateMachineScheme scheme = new StateMachineScheme();
        scheme.setStatus(StateMachineSchemeStatus.CREATE);
        scheme.setName(name);
        scheme.setDescription(name);
        scheme.setOrganizationId(organizationId);
        //保证幂等性
        List<StateMachineScheme> stateMachines = schemeMapper.select(scheme);
        if (stateMachines.isEmpty()) {
            int isInsert = schemeMapper.insert(scheme);
            if (isInsert != 1) {
                throw new CommonException("error.stateMachineScheme.create");
            }
            //创建默认状态机配置
            configService.createDefaultConfig(organizationId, scheme.getId(), stateMachineId);

            //创建与项目的关联关系
            projectConfigService.create(projectId, scheme.getId(), SchemeType.STATE_MACHINE, schemeApplyType);
        }
    }

    @Override
    public void activeSchemeWithRefProjectConfig(Long schemeId) {
        StateMachineScheme scheme = schemeMapper.selectByPrimaryKey(schemeId);
        //活跃状态机方案
        if (scheme.getStatus().equals(StateMachineSchemeStatus.CREATE)) {
            scheme.setStatus(StateMachineSchemeStatus.ACTIVE);
            int result = updateOptional(scheme, "status");
            if (result != 1) {
                throw new CommonException("error.stateMachineScheme.activeScheme");
            }
        }
        //复制草稿配置到发布
        configService.copyDraftToDeploy(false, scheme.getOrganizationId(), schemeId);
        //活跃方案下的所有新建状态机
        List<StateMachineSchemeConfigDTO> configs = configService.queryBySchemeId(false, scheme.getOrganizationId(), schemeId);
        stateMachineFeignClient.activeStateMachines(scheme.getOrganizationId(), configs.stream().map(StateMachineSchemeConfigDTO::getStateMachineId).distinct().collect(Collectors.toList()));
    }

    @Override
    public Boolean updateDeployProgress(Long organizationId, Long schemeId, Integer deployProgress) {
        int update = schemeMapper.updateDeployProgress(organizationId, schemeId, deployProgress);
        if (update == 1) {
            //若已完成，更新发布状态
            if (deployProgress.equals(100)) {
                schemeMapper.updateDeployStatus(organizationId, schemeId, "done");
            }
            return true;
        } else {
            return false;
        }
    }
}
