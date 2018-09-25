package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.ProjectConfigDetailDTO;
import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.FieldConfigLine;

import java.util.List;

/**
 * @author shinan.chen
 * @Date 2018/9/4
 */
public interface ProjectConfigService {
    /**
     * 根据问题类型和页面类型获取页面字段
     *
     * @param organizationId
     * @param projectId
     * @param issueTypeId
     * @param pageType
     * @return
     */
    List<Field> queryFieldByIssueTypeAndPageType(Long organizationId, Long projectId, Long issueTypeId, String pageType);

    /**
     * 根据问题类型获取字段配置信息
     *
     * @param organizationId
     * @param projectId
     * @param issueTypeId
     * @return
     */

    List<FieldConfigLine> queryFieldConfigLinesByIssueType(Long organizationId, Long projectId, Long issueTypeId);


    /**
     * 根据项目ID获取问题类型
     *
     * @param projectConfigDetailDTO
     * @return
     */
    ProjectConfigDetailDTO queryIssueTypeByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO);

    /**
     * 根据项目ID获取状态机
     *
     * @param projectConfigDetailDTO
     * @return
     */
    ProjectConfigDetailDTO queryStateMachineByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO);

    /**
     * 根据项目ID获取问题类型页面方案
     *
     * @param projectConfigDetailDTO
     * @return
     */
    ProjectConfigDetailDTO queryPageIssueByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO);

    /**
     * 根据项目ID获取字段配置方案
     *
     * @param projectConfigDetailDTO
     * @return
     */
    ProjectConfigDetailDTO queryFieldConfigByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO);

    ProjectConfigDetailDTO updateFieldConfigByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO);

    ProjectConfigDetailDTO updateIssueTypeByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO);

    ProjectConfigDetailDTO updateStateMachineByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO);

    ProjectConfigDetailDTO updatePageIssueByProjectId(ProjectConfigDetailDTO projectConfigDetailDTO);
}
