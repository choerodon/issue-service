package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.PageIssueTypeSchemeDTO;
import io.choerodon.issue.domain.PageIssueScheme;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseService;

/**
 * @author peng.jiang
 * @Date 2018/8/27
 */
public interface PageIssueSchemeService extends BaseService<PageIssueScheme> {

    /**
     * 分页查询问题类型页面方案
     * @param pageRequest 分页对象
     * @param schemeDTO 查询参数
     * @param params 模糊查询参数
     * @return 状态机方案列表
     */
    Page<PageIssueTypeSchemeDTO> pageQuery(PageRequest pageRequest, PageIssueTypeSchemeDTO schemeDTO, String params);

    /**
     * 创建问题类型页面方案
     * @param organizationId 组织id
     * @param schemeDTO 问题类型页面方案对象
     * @return 状态机方案对象
     */
    PageIssueTypeSchemeDTO create(Long organizationId, PageIssueTypeSchemeDTO schemeDTO);

    /**
     * 更新问题类型页面方案
     * @param organizationId 组织id
     * @param schemeId 方案id
     * @param schemeDTO 问题类型页面方案对象
     * @return 方案对象
     */
    PageIssueTypeSchemeDTO update(Long organizationId, Long schemeId, PageIssueTypeSchemeDTO schemeDTO);

    /**
     * 删除问题类型页面方案
     * @param organizationId 组织id
     * @param schemeId 方案id
     * @return
     */
    Boolean delete(Long organizationId, Long schemeId);

    /**
     * 校验名字是否未被使用
     * @param organizationId 组织id
     * @param schemeId 校验id
     * @param name 名称
     * @return
     */
    Boolean checkName(Long organizationId, Long schemeId, String name);

    /**
     * 获取问题类型页面方案及其配置
     * @param organizationId 组织id
     * @param schemeId 方案id
     * @return 状态机方案及配置
     */
    PageIssueTypeSchemeDTO querySchemeWithConfigById(Long organizationId, Long schemeId);

}
