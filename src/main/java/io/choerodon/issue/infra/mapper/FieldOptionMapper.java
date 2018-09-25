package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.FieldOption;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author jiameng.cao
 * @Date 2018/8/21
 */
@Component
public interface FieldOptionMapper extends BaseMapper<FieldOption> {
    List<FieldOption> queryByFieldId(@Param("fieldId") Long fieldId, @Param("parentId") Long parentId);

    void deleteByFieldId(@Param("fieldId") Long fieldId);

}
