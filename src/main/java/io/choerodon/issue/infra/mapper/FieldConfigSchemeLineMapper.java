package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.FieldConfigSchemeLine;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2018/8/27
 */
public interface FieldConfigSchemeLineMapper extends BaseMapper<FieldConfigSchemeLine> {
    List<FieldConfigSchemeLine> queryBySchemeId(@Param("schemeId") Long schemeId);

    void deleteBySchemeId(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);
}
