package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.PageSchemeLine;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/23
 */
@Component
public interface PageSchemeLineMapper extends BaseMapper<PageSchemeLine> {

    void deleteBySchemeId(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);

    List<PageSchemeLine> queryBySchemeId(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);
}
