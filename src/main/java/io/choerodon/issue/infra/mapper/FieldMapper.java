package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jiameng.cao
 * @Date 2018/8/21
 */
@Component
public interface FieldMapper extends BaseMapper<Field> {
    List<Field> fulltextSearch(@Param("field") Field field, @Param("param") String param);

    List<Field> queryByOrgId(@Param("organizationId") Long organizationId);

    int updateByFieldId(@Param("field") Field field);

    List<Field> queryByPageId(@Param("organizationId") Long organizationId, @Param("pageId") Long pageId);

    List<Page> queryPageByFieldId(@Param("fieldId") Long fieldId);
}
