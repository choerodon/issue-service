package io.choerodon.issue.api.service.impl;


import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.*;
import io.choerodon.issue.api.service.*;
import io.choerodon.issue.domain.*;
import io.choerodon.issue.infra.enums.SchemeType;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.StatusDTO;
import io.choerodon.issue.infra.feign.dto.TransformDTO;
import io.choerodon.issue.infra.mapper.*;
import io.choerodon.issue.infra.utils.ProjectUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

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
    public ProjectConfig create(Long projectId, Long stateMachineSchemeId, Long issueTypeSchemeId) {
        ProjectConfig projectConfig = new ProjectConfig();
        projectConfig.setProjectId(projectId);
        //保证幂等性
        List<ProjectConfig> configs = projectConfigMapper.select(projectConfig);
        if (!configs.isEmpty()) {
            return configs.get(0);
        }
        projectConfig.setStateMachineSchemeId(stateMachineSchemeId);
        projectConfig.setIssueTypeSchemeId(issueTypeSchemeId);
        int result = projectConfigMapper.insert(projectConfig);
        if (result != 1) {
            throw new CommonException("error.projectConfig.create");
        }
        return projectConfig;
    }

    @Override
    public ProjectConfigDetailDTO queryById(Long projectId) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectId);
        ProjectConfigDetailDTO projectConfigDetailDTO = modelMapper.map(projectConfig, ProjectConfigDetailDTO.class);
        //获取问题类型方案
        if (projectConfig.getIssueTypeSchemeId() != null) {
            IssueTypeSchemeDTO issueTypeSchemeDTO = issueTypeSchemeService.queryById(organizationId, projectConfig.getIssueTypeSchemeId());
            projectConfigDetailDTO.setIssueTypeScheme(issueTypeSchemeDTO);
        }
        //获取状态机方案
        if (projectConfig.getStateMachineSchemeId() != null) {
            StateMachineSchemeDTO stateMachineSchemeDTO = stateMachineSchemeService.querySchemeWithConfigById(organizationId, projectConfig.getStateMachineSchemeId());
            projectConfigDetailDTO.setStateMachineScheme(stateMachineSchemeDTO);
        }
        //获取问题类型页面方案
        if (projectConfig.getPageIssueTypeSchemeId() != null) {
            PageIssueSchemeDTO pageIssueSchemeDTO = pageIssueSchemeService.querySchemeWithConfigById(organizationId, projectConfig.getPageIssueTypeSchemeId());
            projectConfigDetailDTO.setPageIssueSchemeDTO(pageIssueSchemeDTO);
        }
        //获取字段配置方案
        if (projectConfig.getFieldConfigSchemeId() != null) {
            FieldConfigSchemeDetailDTO fieldConfigSchemeDTO = fieldConfigSchemeService.querySchemeWithConfigById(organizationId, projectConfig.getFieldConfigSchemeId());
            projectConfigDetailDTO.setFieldConfigSchemeDetailDTO(fieldConfigSchemeDTO);
        }
        return projectConfigDetailDTO;
    }

    @Override
    public List<Field> queryFieldByIssueTypeAndPageType(Long organizationId, Long projectId, Long issueTypeId, String pageType) {

        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectId);

        if (projectConfig.getPageIssueTypeSchemeId() == null) {
            throw new CommonException("error.projectConfig.pageIssueTypeSchemeId.null");
        }

        Long pageSchemeId = pageIssueSchemeLineService.getPageSchemeIdByIssueTypeId(projectConfig.getPageIssueTypeSchemeId(), issueTypeId);

        Long pageId = pageSchemeLineService.getPageIdByPageType(pageSchemeId, pageType);

        List<Field> fields = fieldMapper.queryByPageId(organizationId, pageId);

        return fields;
    }

    @Override
    public List<FieldConfigLine> queryFieldConfigLinesByIssueType(Long organizationId, Long projectId, Long issueTypeId) {
        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectId);

        if (projectConfig.getFieldConfigSchemeId() == null) {
            throw new CommonException("error.projectConfig.fieldConfigSchemeId.null");
        }

        Long fieldConfigId = fieldConfigSchemeLineService.getFieldConfigIdByIssueTypeId(projectConfig.getFieldConfigSchemeId(), issueTypeId);

        List<FieldConfigLine> fieldConfigLines = fieldConfigLineMapper.queryByFieldConfigId(fieldConfigId);

        //过滤掉隐藏字段
        return fieldConfigLines.stream().filter(x -> x.getIsDisplay().equals(YES)).collect(Collectors.toList());
    }

    @Override
    public List<IssueTypeDTO> queryIssueTypesByProjectId(Long projectId, String schemeType) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectId);
        //敏捷信息
        if (schemeType.equals(SchemeType.AGILE)) {
            //获取问题类型方案
            if (projectConfig.getIssueTypeSchemeId() != null) {
                //根据方案配置表获取 问题类型
                List<IssueType> issueTypes = issueTypeMapper.queryBySchemeId(organizationId, projectConfig.getIssueTypeSchemeId());
                return modelMapper.map(issueTypes, new TypeToken<List<IssueTypeDTO>>() {
                }.getType());
            } else {
                throw new CommonException("error.queryIssueTypesByProjectId.issueTypeSchemeId.null");
            }
        }
        return null;
    }

    @Override
    public List<IssueTypeWithStateMachineIdDTO> queryIssueTypesWithStateMachineIdByProjectId(Long projectId, String schemeType) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectId);
        //敏捷信息
        if (schemeType.equals(SchemeType.AGILE)) {
            Long issueTypeSchemeId = projectConfig.getIssueTypeSchemeId();
            Long stateMachineSchemeId = projectConfig.getStateMachineSchemeId();

            if (issueTypeSchemeId == null) {
                throw new CommonException("error.queryIssueTypesByProjectId.issueTypeSchemeId.null");
            }
            if (stateMachineSchemeId == null) {
                throw new CommonException("error.queryIssueTypesByProjectId.getStateMachineSchemeId.null");
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
        return null;
    }

    @Override
    public List<TransformDTO> queryTransformsByProjectId(Long projectId, Long currentStatusId, Long issueId, Long issueTypeId, String schemeType) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectId);
        //敏捷信息
        if (schemeType.equals(SchemeType.AGILE)) {
            //获取状态机方案
            if (projectConfig.getStateMachineSchemeId() != null) {
                //获取状态机
                Long stateMachineId = stateMachineService.queryBySchemeIdAndIssueTypeId(projectConfig.getStateMachineSchemeId(), issueTypeId);
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
        return null;
    }

    @Override
    public Long queryStateMachineId(Long projectId, String schemeType, Long issueTypeId) {
        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectId);
        //敏捷信息
        if (schemeType.equals(SchemeType.AGILE)) {
            Long issueTypeSchemeId = projectConfig.getIssueTypeSchemeId();
            Long stateMachineSchemeId = projectConfig.getStateMachineSchemeId();

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
        return null;
    }

    @Override
    public StatusDTO createStatusForAgile(Long projectId, StatusDTO statusDTO) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        statusDTO.setOrganizationId(organizationId);
        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectId);
        Long stateMachineSchemeId = projectConfig.getStateMachineSchemeId();
        //校验状态机方案是否只关联一个项目
        ProjectConfig select = new ProjectConfig();
        select.setStateMachineSchemeId(stateMachineSchemeId);
        if (projectConfigMapper.select(select).size() > 1) {
            throw new CommonException("error.createStatusForAgile.multiScheme");
        }
        //校验状态机方案是否只有一个状态机
        StateMachineScheme stateMachineScheme = stateMachineSchemeMapper.selectByPrimaryKey(stateMachineSchemeId);
        StateMachineSchemeConfig schemeConfig = new StateMachineSchemeConfig();
        schemeConfig.setStateMachineId(stateMachineSchemeId);
        if (!stateMachineSchemeConfigMapper.select(schemeConfig).isEmpty()) {
            throw new CommonException("error.createStatusForAgile.multiScheme");
        }

        Long stateMachineId = stateMachineScheme.getDefaultStateMachineId();

        statusDTO = stateMachineFeignClient.createStatusForAgile(organizationId, stateMachineId, statusDTO).getBody();
        return statusDTO;
    }
}
