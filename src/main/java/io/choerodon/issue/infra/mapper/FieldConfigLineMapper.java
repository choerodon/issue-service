package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.FieldConfigLine;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2018/8/23
 */

@Component
public interface FieldConfigLineMapper extends BaseMapper<FieldConfigLine> {
    List<FieldConfigLine> queryByFieldConfigId(@Param("id") Long id);

    FieldConfigLine queryById(@Param("id") Long id);

    int deleteByFieldId(@Param("fieldId") Long fieldId);

    List<FieldConfigLine> searchFull(@Param("fieldConfigId") Long fieldConfigId, @Param("param") String param);

    int deleteByFieldConfigId(@Param("id") Long id);
}
