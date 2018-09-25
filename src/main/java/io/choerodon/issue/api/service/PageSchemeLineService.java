package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.PageSchemeLineDTO;
import io.choerodon.issue.domain.PageSchemeLine;
import io.choerodon.mybatis.service.BaseService;

import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/23
 */
public interface PageSchemeLineService extends BaseService<PageSchemeLine> {

    /**
     * 查询方案配置
     *
     * @param organizationId
     * @param pageSchemeLineId
     * @return
     */
    PageSchemeLineDTO queryById(Long organizationId, Long pageSchemeLineId);

    /**
     * 创建方案配置
     *
     * @param organizationId
     * @param pageSchemeLineDTO
     * @return
     */
    PageSchemeLineDTO create(Long organizationId, PageSchemeLineDTO pageSchemeLineDTO);

    /**
     * 更新方案配置
     *
     * @param organizationId
     * @param pageSchemeLineDTO
     * @return
     */
    PageSchemeLineDTO update(Long organizationId, PageSchemeLineDTO pageSchemeLineDTO);

    /**
     * 校验是否可以删除
     *
     * @param organizationId
     * @param pageSchemeLineId
     * @return
     */
    Map<String, Object> checkDelete(Long organizationId, Long pageSchemeLineId);

    /**
     * 删除
     *
     * @param organizationId
     * @param pageSchemeLineId
     * @return
     */
    Boolean delete(Long organizationId, Long pageSchemeLineId);

    /**
     * 校验类型是否已存在
     *
     * @param organizationId
     * @param schemeId
     * @param type
     */
    void checkUniqueType(Long organizationId, Long schemeId, String type);

    /**
     * 通过页面类型获取到页面id（默认值的处理）
     * @param schemeId
     * @param pageType
     * @return
     */
    Long getPageIdByPageType(Long schemeId, String pageType);
}
