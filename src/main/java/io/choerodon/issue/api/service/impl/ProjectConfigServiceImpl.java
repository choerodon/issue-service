package io.choerodon.issue.api.service.impl;


import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.*;
import io.choerodon.issue.api.service.*;
import io.choerodon.issue.domain.*;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.mapper.*;
import io.choerodon.issue.infra.utils.ProjectUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author shinan.chen
 * @Date 2018/9/4
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
    public ProjectConfigDetailDTO queryIssueTypeByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO) {
        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectConfigDetailDTO.getProjectId());
        if (projectConfig.getIssueTypeSchemeId() == null) {
            throw new CommonException("error.projectConfig.issueTypeSchemeId.null");
        }

        IssueTypeScheme issueTypeScheme = issueTypeSchemeService.selectByPrimaryKey(projectConfig.getIssueTypeSchemeId());
        projectConfigDetailDTO.setIssueTypeSchemeName(issueTypeScheme.getName());
        projectConfigDetailDTO.setIssueTypeSchemeId(issueTypeScheme.getId());

        IssueTypeSchemeConfig issueTypeSchemeConfig = new IssueTypeSchemeConfig();
        issueTypeSchemeConfig.setSchemeId(issueTypeScheme.getId());
        List<IssueTypeSchemeConfig> issueTypeSchemeConfigList = issueTypeSchemeConfigMapper.select(issueTypeSchemeConfig);
        List<IssueType> issueTypeList = new ArrayList<>();
        for (IssueTypeSchemeConfig record : issueTypeSchemeConfigList) {
            IssueType issueType = issueTypeMapper.selectByPrimaryKey(record.getIssueTypeId());
            issueTypeList.add(issueType);
        }

        List<IssueTypeDTO> issueTypeDTOList = modelMapper.map(issueTypeList, new TypeToken<List<IssueTypeDTO>>() {
        }.getType());
        projectConfigDetailDTO.setIssueTypeDTOList(issueTypeDTOList);
        return projectConfigDetailDTO;
    }

    @Override
    public ProjectConfigDetailDTO queryStateMachineByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO) {
        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectConfigDetailDTO.getProjectId());
        if (projectConfig.getStateMachineSchemeId() == null) {
            throw new CommonException("error.projectConfig.stateMachineSchemeId.null");
        }

        StateMachineScheme stateMachineScheme = stateMachineSchemeService.selectByPrimaryKey(projectConfig.getStateMachineSchemeId());
        projectConfigDetailDTO.setStateMachineSchemeName(stateMachineScheme.getName());
        projectConfigDetailDTO.setStateMachineSchemeId(stateMachineScheme.getId());

        StateMachineSchemeConfig stateMachineSchemeConfig = new StateMachineSchemeConfig();
        stateMachineSchemeConfig.setSchemeId(stateMachineScheme.getId());
        List<StateMachineSchemeConfig> stateMachineSchemeConfigList = stateMachineSchemeConfigMapper.select(stateMachineSchemeConfig);
//        List<StateMachine> stateMachineList = new ArrayList<>();
//        for (StateMachineSchemeConfig record : stateMachineSchemeConfigList) {
//            StateMachine stateMachine = stateMachineMapper.selectByPrimaryKey(record.getStateMachineId());
//            stateMachineList.add(stateMachine);
//        }     需要后续添加StateMachine
//
//        List<StateMachineDTO> stateMachineDTOList = modelMapper.map(stateMachineList, new TypeToken<List<StateMachineDTO>>() {
//        }.getType());
//        projectConfigDetailDTO.setStateMachineDTOList(stateMachineDTOList);
        return projectConfigDetailDTO;
    }

    @Override
    public ProjectConfigDetailDTO queryPageIssueByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO) {
        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectConfigDetailDTO.getProjectId());
        if (projectConfig.getPageIssueTypeSchemeId() == null) {
            throw new CommonException("error.projectConfig.pageIssueSchemeId.null");
        }

        PageIssueScheme pageIssueScheme = pageIssueSchemeService.selectByPrimaryKey(projectConfig.getPageIssueTypeSchemeId());
        projectConfigDetailDTO.setPageIssueTypeSchemeName(pageIssueScheme.getName());
        projectConfigDetailDTO.setPageIssueTypeSchemeId(pageIssueScheme.getId());

        PageIssueSchemeLine pageIssueSchemeLine = new PageIssueSchemeLine();
        pageIssueSchemeLine.setSchemeId(pageIssueScheme.getId());
        List<PageIssueSchemeLine> pageIssueSchemeLineList = pageIssueSchemeLineMapper.select(pageIssueSchemeLine);
        List<PageScheme> pageSchemeList = new ArrayList<>();
        for (PageIssueSchemeLine record : pageIssueSchemeLineList) {
            PageScheme pageScheme = pageSchemeMapper.selectByPrimaryKey(record.getPageSchemeId());
            pageSchemeList.add(pageScheme);
        }

        List<PageSchemeDetailDTO> pageSchemeDTOList = modelMapper.map(pageSchemeList, new TypeToken<List<PageSchemeDetailDTO>>() {
        }.getType());
        projectConfigDetailDTO.setPageSchemeDTOList(pageSchemeDTOList);
        return projectConfigDetailDTO;
    }

    @Override
    public ProjectConfigDetailDTO queryFieldConfigByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO) {
        ProjectConfig projectConfig = projectConfigMapper.queryByProjectId(projectConfigDetailDTO.getProjectId());
        if (projectConfig.getFieldConfigSchemeId() == null) {
            throw new CommonException("error.projectConfig.fieldConfigSchemeId.null");
        }

        FieldConfigScheme fieldConfigScheme = fieldConfigSchemeService.selectByPrimaryKey(projectConfig.getFieldConfigSchemeId());
        projectConfigDetailDTO.setFieldConfigSchemeName(fieldConfigScheme.getName());
        projectConfigDetailDTO.setFieldConfigSchemeId(fieldConfigScheme.getId());

        FieldConfigSchemeLine fieldConfigSchemeLine = new FieldConfigSchemeLine();
        fieldConfigSchemeLine.setSchemeId(fieldConfigScheme.getId());
        List<FieldConfigSchemeLine> fieldConfigSchemeLineList = fieldConfigSchemeLineMapper.select(fieldConfigSchemeLine);
        List<FieldConfig> fieldConfigList = new ArrayList<>();
        for (FieldConfigSchemeLine record : fieldConfigSchemeLineList) {
            FieldConfig fieldConfig = fieldConfigMapper.selectByPrimaryKey(record.getFieldConfigId());
            fieldConfigList.add(fieldConfig);
        }


        List<FieldConfigDetailDTO> fieldConfigDTOList = modelMapper.map(fieldConfigList, new TypeToken<List<FieldConfigDetailDTO>>() {
        }.getType());
        projectConfigDetailDTO.setFieldConfigDTOList(fieldConfigDTOList);
        return projectConfigDetailDTO;
    }

    @Override
    public ProjectConfigDetailDTO updateFieldConfigByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO) {
        FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO = new FieldConfigSchemeDetailDTO();
        fieldConfigSchemeDetailDTO.setId(projectConfigDetailDTO.getFieldConfigSchemeId());
        fieldConfigSchemeDetailDTO.setName(projectConfigDetailDTO.getFieldConfigSchemeName());
        Long organizationId = projectUtil.getOrganizationId(projectConfigDetailDTO.getProjectId());
        fieldConfigSchemeService.update(organizationId, fieldConfigSchemeDetailDTO);

        FieldConfigDetailDTO fieldConfigDetailDTO = new FieldConfigDetailDTO();
        fieldConfigDetailDTO.setOrganizationId(organizationId);
        fieldConfigDetailDTO = projectConfigDetailDTO.getFieldConfigDTOList().get(0);
        fieldConfigService.update(fieldConfigDetailDTO);
        return projectConfigDetailDTO;
    }

    @Override
    public ProjectConfigDetailDTO updateIssueTypeByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO) {
        IssueTypeSchemeDTO issueTypeSchemeDTO = new IssueTypeSchemeDTO();
        issueTypeSchemeDTO.setId(projectConfigDetailDTO.getIssueTypeSchemeId());
        issueTypeSchemeDTO.setName(projectConfigDetailDTO.getIssueTypeSchemeName());
        Long organizationId = projectUtil.getOrganizationId(projectConfigDetailDTO.getProjectId());
        issueTypeSchemeService.update(organizationId, issueTypeSchemeDTO);

        IssueTypeDTO issueTypeDTO = new IssueTypeDTO();
        issueTypeDTO.setOrganizationId(organizationId);
        List<IssueTypeDTO> issueTypeDTOList = projectConfigDetailDTO.getIssueTypeDTOList();
        for (IssueTypeDTO record : issueTypeDTOList) {
            issueTypeService.update(record);
        }
        return projectConfigDetailDTO;
    }

    @Override
    public ProjectConfigDetailDTO updateStateMachineByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO) {
        StateMachineSchemeDTO stateMachineSchemeDTO = new StateMachineSchemeDTO();
        stateMachineSchemeDTO.setId(projectConfigDetailDTO.getStateMachineSchemeId());
        stateMachineSchemeDTO.setName(projectConfigDetailDTO.getStateMachineSchemeName());
        Long organizationId = projectUtil.getOrganizationId(projectConfigDetailDTO.getProjectId());
        stateMachineSchemeService.update(organizationId, stateMachineSchemeDTO.getId(), stateMachineSchemeDTO);

        StateMachineDTO stateMachineDTO = new StateMachineDTO();
        stateMachineDTO.setOrganizationId(organizationId);
        List<StateMachineDTO> stateMachineDTOList = projectConfigDetailDTO.getStateMachineDTOList();
        for (StateMachineDTO record : stateMachineDTOList) {
            //          stateMachineService.update(record);
        }
        return projectConfigDetailDTO;
    }

    @Override
    public ProjectConfigDetailDTO updatePageIssueByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO) {

        PageIssueSchemeDTO pageIssueSchemeDTO = new PageIssueSchemeDTO();
        pageIssueSchemeDTO.setId(projectConfigDetailDTO.getPageIssueTypeSchemeId());
        pageIssueSchemeDTO.setName(projectConfigDetailDTO.getIssueTypeSchemeName());
        Long organizationId = projectUtil.getOrganizationId(projectConfigDetailDTO.getProjectId());
        pageIssueSchemeService.update(organizationId, pageIssueSchemeDTO.getId(), pageIssueSchemeDTO);

        PageSchemeDetailDTO pageSchemeDetailDTO = new PageSchemeDetailDTO();
        pageSchemeDetailDTO.setOrganizationId(organizationId);
        List<PageSchemeDetailDTO> pageSchemeDTOList = projectConfigDetailDTO.getPageSchemeDTOList();
        for (PageSchemeDetailDTO record : pageSchemeDTOList) {
            pageSchemeService.update(organizationId, record);
        }
        return projectConfigDetailDTO;
    }
}
