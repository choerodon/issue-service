package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.FieldConfigScheme;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2018/8/27
 */
@Component
public interface FieldConfigSchemeMapper extends BaseMapper<FieldConfigScheme> {
    List<FieldConfigScheme> fulltextSearch(@Param("fieldConfigScheme") FieldConfigScheme fieldConfigScheme, @Param("param") String param);

    List<FieldConfigScheme> queryByFieldConfigId(@Param("organizationId") Long organizationId, @Param("fieldConfigId") Long fieldConfigId);

}
