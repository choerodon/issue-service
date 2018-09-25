package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.IssueType;
import io.choerodon.issue.domain.PageIssueScheme;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public interface PageIssueSchemeMapper extends BaseMapper<PageIssueScheme> {

    /**
     * 分页查询问题类型页面方案
     *
     * @param scheme 状态机方案
     * @param param  模糊查询参数
     * @return 方案列表
     */
    List<PageIssueScheme> fulltextSearch(@Param("scheme") PageIssueScheme scheme, @Param("param") String param);

    /**
     * 获取问题类型页面方案及其配置
     *
     * @param schemeId 问题类型页面方案id
     * @return 问题类型页面方案及配置
     */
    PageIssueScheme getSchemeWithConfigById(Long schemeId);

    /**
     * 查询未使用到的问题类型
     *
     * @param organizationId 组织id
     * @param schemeId       方案id
     * @return
     */
    List<IssueType> queryIssueType(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);

    /**
     * 根据页面方案Id查询出有关的问题类型页面方案
     * @param organizationId
     * @param pageSchemeId
     * @return
     */
    List<PageIssueScheme> queryByPageSchemeId(@Param("organizationId") Long organizationId, @Param("pageSchemeId") Long pageSchemeId);
}
