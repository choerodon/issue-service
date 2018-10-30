package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.dto.IssueTypeWithStateMachineIdDTO;
import io.choerodon.issue.api.dto.ProjectConfigDetailDTO;
import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.FieldConfigLine;
import io.choerodon.issue.domain.ProjectConfig;
import io.choerodon.issue.infra.feign.dto.TransformDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @Date 2018/9/4
 */
public interface ProjectConfigService {

    /**
     * 创建项目方案配置
     *
     * @param projectId
     * @param stateMachineSchemeId
     * @return
     */
    ProjectConfig create(Long projectId, Long stateMachineSchemeId, Long issueTypeSchemeId);

    /**
     * 获取项目配置方案信息
     *
     * @param projectId
     * @return
     */
    ProjectConfigDetailDTO queryById(Long projectId);

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
     * 根据项目id找到方案返回问题类型列表
     *
     * @param projectId
     * @param schemeType
     * @return
     */
    List<IssueTypeDTO> queryIssueTypesByProjectId(Long projectId, String schemeType);

    /**
     * 根据项目id找到方案返回问题类型列表，带问题类型对应的状态机id
     *
     * @param projectId
     * @param schemeType
     * @return
     */
    List<IssueTypeWithStateMachineIdDTO> queryIssueTypesWithStateMachineIdByProjectId(Long projectId, String schemeType);

    /**
     * 根据项目id找到方案返回当前状态可以转换的列表
     *
     * @param projectId
     * @param currentStatusId
     * @param issueId
     * @param issueTypeId
     * @param schemeType
     * @return
     */
    List<TransformDTO> queryTransformsByProjectId(Long projectId, Long currentStatusId, Long issueId, Long issueTypeId, String schemeType);

    /**
     * 根据项目id找到方案返回问题类型对应的状态机
     *
     * @param projectId
     * @param schemeType
     * @param issueTypeId
     * @return
     */
    Long queryStateMachineId(Long projectId, String schemeType, Long issueTypeId);
}
