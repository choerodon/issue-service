package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.PageFieldRef;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @date 2018/8/22
 */
@Component
public interface PageFieldRefMapper extends BaseMapper<PageFieldRef> {
    void deleteByPageId(@Param("organizationId") Long organizationId, @Param("pageId") Long pageId);
}
