package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.ProjectConfigDetailDTO;
import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.FieldConfigLine;
import io.choerodon.issue.domain.ProjectConfig;

import java.util.List;

/**
 * @author shinan.chen
 * @Date 2018/9/4
 */
public interface ProjectConfigService {

    /**
     * 创建项目方案配置
     * @param projectId
     * @param stateMachineSchemeId
     * @return
     */
    ProjectConfig create(Long projectId, Long stateMachineSchemeId, Long issueTypeSchemeId);

    /**
     * 获取项目配置方案信息
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
}
