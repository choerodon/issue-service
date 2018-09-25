package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.FieldConfig;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jiameng.cao
 * @Date 2018/8/23
 */
@Component
public interface FieldConfigMapper extends BaseMapper<FieldConfig> {
    List<FieldConfig> fulltextSearch(@Param("fieldConfig") FieldConfig fieldConfig, @Param("param") String param);

}
