package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.PageScheme;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/23
 */
@Component
public interface PageSchemeMapper extends BaseMapper<PageScheme> {
    List<PageScheme> fulltextSearch(@Param("pageScheme") PageScheme pageScheme, @Param("param") String param);

    /**
     * 根据页面Id查询出关联的页面方案
     * @param organizationId
     * @param pageId
     * @return
     */
    List<PageScheme> queryByPageId(@Param("organizationId") Long organizationId, @Param("pageId") Long pageId);

}
