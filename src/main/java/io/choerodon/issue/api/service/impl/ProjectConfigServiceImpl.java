package io.choerodon.issue.api.service.impl;


import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.*;
import io.choerodon.issue.api.service.*;
import io.choerodon.issue.domain.*;
import io.choerodon.issue.infra.enums.SchemeApplyType;
import io.choerodon.issue.infra.enums.SchemeType;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.StatusDTO;
import io.choerodon.issue.infra.feign.dto.TransformDTO;
import io.choerodon.issue.infra.mapper.*;
import io.choerodon.issue.infra.utils.EnumUtil;
import io.choerodon.issue.infra.utils.ProjectUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author shinan.chen
 * @Date 2018/10/24
 */
@Component
@RefreshScope
public class ProjectConfigServiceImpl implements ProjectConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectConfigServiceImpl.class);
    private static final String YES = "1";
    private static final String AGILE_SERVICE = "agile-service";
    @Autowired
    private ProjectConfigMapper projectConfigMapper;
    @Autowired
    private FieldMapper fieldMapper;
    @Autowired
    private FieldConfigLineMapper fieldConfigLineMapper;
    @Autowired
    private IssueTypeMapper issueTypeMapper;
    @Autowired
    private IssueTypeSchemeMapper issueTypeSchemeMapper;
    @Autowired
    private IssueTypeSchemeConfigMapper issueTypeSchemeConfigMapper;
    @Autowired
    private StateMachineSchemeConfigMapper stateMachineSchemeConfigMapper;
    @Autowired
    private PageIssueSchemeLineMapper pageIssueSchemeLineMapper;
    @Autowired
    private PageSchemeMapper pageSchemeMapper;
    @Autowired
    private FieldConfigSchemeLineMapper fieldConfigSchemeLineMapper;
    @Autowired
    private FieldConfigMapper fieldConfigMapper;
    @Autowired
    private FieldConfigSchemeMapper fieldConfigSchemeMapper;
    @Autowired
    private PageIssueSchemeLineService pageIssueSchemeLineService;
    @Autowired
    private PageSchemeLineService pageSchemeLineService;
    @Autowired
    private FieldConfigSchemeLineService fieldConfigSchemeLineService;
    @Autowired
    private IssueTypeSchemeService issueTypeSchemeService;
    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;
    @Autowired
    private PageIssueSchemeService pageIssueSchemeService;
    @Autowired
    private FieldConfigSchemeService fieldConfigSchemeService;
    @Autowired
    private FieldConfigService fieldConfigService;
    @Autowired
    private IssueTypeService issueTypeService;
    @Autowired
    private PageSchemeService pageSchemeService;
    @Autowired
    private StateMachineService stateMachineService;
    @Autowired
    private StateMachineFeignClient stateMachineFeignClient;
    @Autowired
    private ProjectUtil projectUtil;
    @Autowired
    private StateMachineSchemeMapper stateMachineSchemeMapper;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public ProjectConfig create(Long projectId, Long schemeId, String schemeType, String applyType) {
        if (!EnumUtil.contain(SchemeType.class, schemeType)) {
            throw new CommonException("error.schemeType.illegal");
        }
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        ProjectConfig projectConfig = new ProjectConfig(projectId, schemeId, schemeType, applyType);
        //保证幂等性
        List<ProjectConfig> configs = projectConfigMapper.select(projectConfig);
        if (!configs.isEmpty()) {
            return configs.get(0);
        }
        int result = projectConfigMapper.insert(projectConfig);
        if (result != 1) {
            throw new CommonException("error.projectConfig.create");
        }
        return projectConfig;
    }

    @Override
    public ProjectConfigDetailDTO queryById(Long projectId) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        List<ProjectConfig> projectConfigs = projectConfigMapper.queryByProjectId(projectId);
        Map<String, List<ProjectConfig>> configMap = projectConfigs.stream().collect(Collectors.groupingBy(ProjectConfig::getSchemeType));
        ProjectConfigDetailDTO projectConfigDetailDTO = new ProjectConfigDetailDTO();
        //获取问题类型方案
        List<ProjectConfig> issueTypeSchemeConfigs = configMap.get(SchemeType.ISSUE_TYPE);
        if (!issueTypeSchemeConfigs.isEmpty()) {
            Map<String, IssueTypeSchemeDTO> issueTypeSchemeMap = new HashMap<>(issueTypeSchemeConfigs.size());
            for (ProjectConfig projectConfig : issueTypeSchemeConfigs) {
                IssueTypeSchemeDTO issueTypeSchemeDTO = issueTypeSchemeService.queryById(organizationId, projectConfig.getSchemeId());
                issueTypeSchemeMap.put(projectConfig.getApplyType(), issueTypeSchemeDTO);
            }
            projectConfigDetailDTO.setIssueTypeSchemeMap(issueTypeSchemeMap);
        }
        //获取状态机方案
        List<ProjectConfig> stateMachineSchemeConfigs = configMap.get(SchemeType.STATE_MACHINE);
        if (!stateMachineSchemeConfigs.isEmpty()) {
            Map<String, StateMachineSchemeDTO> stateMachineSchemeMap = new HashMap<>(stateMachineSchemeConfigs.size());
            for (ProjectConfig projectConfig : stateMachineSchemeConfigs) {
                StateMachineSchemeDTO stateMachineSchemeDTO = stateMachineSchemeService.querySchemeWithConfigById(organizationId, projectConfig.getSchemeId());
                stateMachineSchemeMap.put(projectConfig.getApplyType(), stateMachineSchemeDTO);
            }
            projectConfigDetailDTO.setStateMachineSchemeMap(stateMachineSchemeMap);
        }
        //获取问题类型页面方案
        List<ProjectConfig> pageIssueTypeSchemeConfigs = configMap.get(SchemeType.PAGE_ISSUE_TYPE);
        if (!pageIssueTypeSchemeConfigs.isEmpty()) {
            Map<String, PageIssueTypeSchemeDTO> pageIssueTypeSchemeMap = new HashMap<>(pageIssueTypeSchemeConfigs.size());
            for (ProjectConfig projectConfig : pageIssueTypeSchemeConfigs) {
                PageIssueTypeSchemeDTO pageIssueTypeSchemeDTO = pageIssueSchemeService.querySchemeWithConfigById(organizationId, projectConfig.getSchemeId());
                pageIssueTypeSchemeMap.put(projectConfig.getApplyType(), pageIssueTypeSchemeDTO);
            }
            projectConfigDetailDTO.setPageIssueTypeSchemeMap(pageIssueTypeSchemeMap);
        }
        //获取字段配置方案
        List<ProjectConfig> fieldConfigSchemeConfigs = configMap.get(SchemeType.FIELD_CONFIG);
        if (!fieldConfigSchemeConfigs.isEmpty()) {
            Map<String, FieldConfigSchemeDetailDTO> fieldConfigSchemeMap = new HashMap<>(fieldConfigSchemeConfigs.size());
            for (ProjectConfig projectConfig : fieldConfigSchemeConfigs) {
                FieldConfigSchemeDetailDTO fieldConfigSchemeDTO = fieldConfigSchemeService.querySchemeWithConfigById(organizationId, projectConfig.getSchemeId());
                fieldConfigSchemeMap.put(projectConfig.getApplyType(), fieldConfigSchemeDTO);
            }
            projectConfigDetailDTO.setFieldConfigchemeMap(fieldConfigSchemeMap);
        }
        return projectConfigDetailDTO;
    }

    @Override
    public List<Field> queryFieldByIssueTypeAndPageType(Long organizationId, Long projectId, Long issueTypeId, String pageType) {

        ProjectConfig projectConfig = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.PAGE_ISSUE_TYPE, SchemeApplyType.CLOOPM);
        Long pageSchemeId = pageIssueSchemeLineService.getPageSchemeIdByIssueTypeId(projectConfig.getSchemeId(), issueTypeId);
        Long pageId = pageSchemeLineService.getPageIdByPageType(pageSchemeId, pageType);
        List<Field> fields = fieldMapper.queryByPageId(organizationId, pageId);

        return fields;
    }

    @Override
    public List<FieldConfigLine> queryFieldConfigLinesByIssueType(Long organizationId, Long projectId, Long issueTypeId) {

        ProjectConfig projectConfig = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.FIELD_CONFIG, SchemeApplyType.CLOOPM);
        Long fieldConfigId = fieldConfigSchemeLineService.getFieldConfigIdByIssueTypeId(projectConfig.getSchemeId(), issueTypeId);
        List<FieldConfigLine> fieldConfigLines = fieldConfigLineMapper.queryByFieldConfigId(fieldConfigId);

        //过滤掉隐藏字段
        return fieldConfigLines.stream().filter(x -> x.getIsDisplay().equals(YES)).collect(Collectors.toList());
    }

    @Override
    public List<IssueTypeDTO> queryIssueTypesByProjectId(Long projectId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        Long organizationId = projectUtil.getOrganizationId(projectId);
        ProjectConfig projectConfig = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.ISSUE_TYPE, applyType);
        //获取问题类型方案
        if (projectConfig.getSchemeId() != null) {
            //根据方案配置表获取 问题类型
            List<IssueType> issueTypes = issueTypeMapper.queryBySchemeId(organizationId, projectConfig.getSchemeId());
            return modelMapper.map(issueTypes, new TypeToken<List<IssueTypeDTO>>() {
            }.getType());
        } else {
            throw new CommonException("error.queryIssueTypesByProjectId.issueTypeSchemeId.null");
        }
    }

    @Override
    public List<IssueTypeWithStateMachineIdDTO> queryIssueTypesWithStateMachineIdByProjectId(Long projectId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long issueTypeSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.ISSUE_TYPE, applyType).getSchemeId();
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (issueTypeSchemeId == null) {
            throw new CommonException("error.issueTypeSchemeId.null");
        }
        if (stateMachineSchemeId == null) {
            throw new CommonException("error.stateMachineSchemeId.null");
        }
        //根据方案配置表获取 问题类型
        List<IssueType> issueTypes = issueTypeMapper.queryBySchemeId(organizationId, issueTypeSchemeId);
        //根据方案配置表获取 状态机与问题类型的对应关系
        StateMachineScheme stateMachineScheme = stateMachineSchemeMapper.selectByPrimaryKey(stateMachineSchemeId);
        StateMachineSchemeConfig config = new StateMachineSchemeConfig();
        config.setSchemeId(stateMachineSchemeId);
        List<StateMachineSchemeConfig> configs = stateMachineSchemeConfigMapper.select(config);
        Map<Long, Long> map = configs.stream().collect(Collectors.toMap(StateMachineSchemeConfig::getIssueTypeId, StateMachineSchemeConfig::getStateMachineId));

        List<IssueTypeWithStateMachineIdDTO> issueTypeWithStateMachineIds = modelMapper.map(issueTypes, new TypeToken<List<IssueTypeWithStateMachineIdDTO>>() {
        }.getType());
        issueTypeWithStateMachineIds.forEach(x -> {
            Long stateMachineId = map.get(x.getId());
            if (stateMachineId != null) {
                x.setStateMachineId(stateMachineId);
            } else {
                x.setStateMachineId(stateMachineScheme.getDefaultStateMachineId());
            }
        });
        return issueTypeWithStateMachineIds;
    }

    @Override
    public List<StatusDTO> queryStatusByIssueTypeId(Long projectId, Long issueTypeId, String applyType) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (stateMachineSchemeId == null) {
            throw new CommonException("error.stateMachineSchemeId.null");
        }
        //获取状态机
        Long stateMachineId = stateMachineService.queryBySchemeIdAndIssueTypeId(stateMachineSchemeId, issueTypeId);
        return stateMachineFeignClient.queryByStateMachineIds(organizationId, Collections.singletonList(stateMachineId)).getBody();
    }

    @Override
    public List<StatusDTO> queryStatusByProjectId(Long projectId, String applyType) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (stateMachineSchemeId == null) {
            throw new CommonException("error.stateMachineSchemeId.null");
        }
        //获取状态机ids
        List<Long> stateMachineIds = stateMachineService.queryBySchemeId(stateMachineSchemeId);
        return stateMachineFeignClient.queryByStateMachineIds(organizationId, stateMachineIds).getBody();
    }

    @Override
    public List<TransformDTO> queryTransformsByProjectId(Long projectId, Long currentStatusId, Long issueId, Long issueTypeId, String applyType) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        Long organizationId = projectUtil.getOrganizationId(projectId);
        ProjectConfig projectConfig = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType);
        //获取状态机方案
        if (projectConfig.getSchemeId() != null) {
            //获取状态机
            Long stateMachineId = stateMachineService.queryBySchemeIdAndIssueTypeId(projectConfig.getSchemeId(), issueTypeId);
            //获取当前状态拥有的转换
            List<TransformDTO> transformDTOS = stateMachineFeignClient.transformList(organizationId, AGILE_SERVICE, stateMachineId, issueId, currentStatusId).getBody();
            //获取组织中所有状态
            List<StatusDTO> statusDTOS = stateMachineFeignClient.queryAllStatus(organizationId).getBody();
            Map<Long, StatusDTO> statusMap = statusDTOS.stream().collect(Collectors.toMap(StatusDTO::getId, x -> x));
            transformDTOS.forEach(transformDTO -> {
                StatusDTO statusDTO = statusMap.get(transformDTO.getEndStatusId());
                transformDTO.setStatusDTO(statusDTO);
            });
            return transformDTOS;
        } else {
            throw new CommonException("error.queryIssueTypesByProjectId.issueTypeSchemeId.null");
        }
    }

    @Override
    public Long queryStateMachineId(Long projectId, String applyType, Long issueTypeId) {
        if (!EnumUtil.contain(SchemeApplyType.class, applyType)) {
            throw new CommonException("error.applyType.illegal");
        }
        Long issueTypeSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.ISSUE_TYPE, applyType).getSchemeId();
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, applyType).getSchemeId();
        if (issueTypeSchemeId == null) {
            throw new CommonException("error.queryStateMachineId.issueTypeSchemeId.null");
        }
        if (stateMachineSchemeId == null) {
            throw new CommonException("error.queryStateMachineId.getStateMachineSchemeId.null");
        }
        StateMachineSchemeConfig config = new StateMachineSchemeConfig();
        config.setSchemeId(stateMachineSchemeId);
        config.setIssueTypeId(issueTypeId);
        List<StateMachineSchemeConfig> configs = stateMachineSchemeConfigMapper.select(config);
        if (!configs.isEmpty()) {
            return configs.get(0).getStateMachineId();
        } else {
            StateMachineScheme stateMachineScheme = stateMachineSchemeMapper.selectByPrimaryKey(stateMachineSchemeId);
            return stateMachineScheme.getDefaultStateMachineId();
        }
    }

    @Override
    public StatusDTO createStatusForAgile(Long projectId, StatusDTO statusDTO) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        statusDTO.setOrganizationId(organizationId);
        Long stateMachineSchemeId = projectConfigMapper.queryBySchemeTypeAndApplyType(projectId, SchemeType.STATE_MACHINE, SchemeApplyType.AGILE).getSchemeId();
        //校验状态机方案是否只关联一个项目
        ProjectConfig select = new ProjectConfig();
        select.setSchemeId(stateMachineSchemeId);
        select.setSchemeType(SchemeType.STATE_MACHINE);
        select.setApplyType(SchemeApplyType.AGILE);
        if (projectConfigMapper.select(select).size() > 1) {
            throw new CommonException("error.createStatusForAgile.multiScheme");
        }
        //校验状态机方案是否只有一个状态机
        StateMachineScheme stateMachineScheme = stateMachineSchemeMapper.selectByPrimaryKey(stateMachineSchemeId);
        StateMachineSchemeConfig schemeConfig = new StateMachineSchemeConfig();
        schemeConfig.setSchemeId(stateMachineSchemeId);
        if (!stateMachineSchemeConfigMapper.select(schemeConfig).isEmpty()) {
            throw new CommonException("error.createStatusForAgile.multiScheme");
        }

        Long stateMachineId = stateMachineScheme.getDefaultStateMachineId();

        statusDTO = stateMachineFeignClient.createStatusForAgile(organizationId, stateMachineId, statusDTO).getBody();
        return statusDTO;
    }

    @Override
    public List<Long> queryProjectIds(Long organizationId, Long stateMachineId) {
        //查询出默认状态机的状态机方案
        StateMachineScheme stateMachineScheme = new StateMachineScheme();
        stateMachineScheme.setDefaultStateMachineId(stateMachineId);
        stateMachineScheme.setOrganizationId(organizationId);
        List<Long> schemeIds = stateMachineSchemeMapper.select(stateMachineScheme).stream().map(StateMachineScheme::getId).collect(Collectors.toList());

        //查询状态机方案中的配置
        StateMachineSchemeConfig schemeConfig = new StateMachineSchemeConfig();
        schemeConfig.setStateMachineId(stateMachineId);
        schemeIds.addAll(stateMachineSchemeConfigMapper.select(schemeConfig).stream().map(StateMachineSchemeConfig::getSchemeId).collect(Collectors.toList()));

        if (!schemeIds.isEmpty()) {
            return projectConfigMapper.queryProjectIdsBySchemeIds(schemeIds);
        }
        return Collections.emptyList();
    }
}
