package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.PageIssueSchemeDTO;
import io.choerodon.issue.api.dto.PageIssueSchemeLineDTO;
import io.choerodon.issue.domain.PageIssueSchemeLine;
import io.choerodon.mybatis.service.BaseService;

/**
 * @author peng.jiang
 * @Date 2018/8/27
 */
public interface PageIssueSchemeLineService extends BaseService<PageIssueSchemeLine> {
    /**
     * 创建问题类型页面方案关联
     * @param organizationId
     * @param schemeId
     * @param lineDTO
     * @return
     */
    PageIssueSchemeDTO create(Long organizationId, Long schemeId, PageIssueSchemeLineDTO lineDTO);

    /**
     * 创建问题类型页面方案关联
     * @param organizationId
     * @param schemeId
     * @param lineDTO
     * @return
     */
    PageIssueSchemeDTO update(Long organizationId, Long schemeId, PageIssueSchemeLineDTO lineDTO);

    /**
     * 根据id删除关联配置
     * @param organizationId 组织id
     * @return
     */
    PageIssueSchemeDTO delete(Long organizationId, Long lineId);

    /**
     * 通过问题类型Id获取到页面方案id（默认值的处理）
     * @param schemeId
     * @param issueTypeId
     * @return
     */
    Long getPageSchemeIdByIssueTypeId(Long schemeId, Long issueTypeId);
}
