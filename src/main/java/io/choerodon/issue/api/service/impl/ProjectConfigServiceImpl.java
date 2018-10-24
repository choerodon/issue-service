package io.choerodon.issue.api.service.impl;


import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.*;
import io.choerodon.issue.api.service.*;
import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.FieldConfigLine;
import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.issue.infra.mapper.*;
import io.choerodon.issue.infra.utils.ProjectUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private ProjectUtil projectUtil;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public ProjectConfig create(Long projectId, Long stateMachineSchemeId, Long issueTypeSchemeId) {
        ProjectConfig projectConfig = new ProjectConfig();
        projectConfig.setProjectId(projectId);
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
}
