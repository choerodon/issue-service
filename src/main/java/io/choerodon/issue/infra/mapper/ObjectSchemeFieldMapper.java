package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.api.vo.ObjectSchemeFieldSearchVO;
import io.choerodon.issue.infra.dto.ObjectSchemeField;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
public interface ObjectSchemeFieldMapper extends Mapper<ObjectSchemeField> {
    /**
     * 根据对象方案编码查询方案字段
     *
     * @param organizationId
     * @return
     */
    List<ObjectSchemeField> listQuery(@Param("organizationId") Long organizationId, @Param("projectId") Long projectId, @Param("searchVO") ObjectSchemeFieldSearchVO searchVO);

    ObjectSchemeField queryById(@Param("fieldId") Long fieldId);

    ObjectSchemeField queryByFieldCode(@Param("organizationId") Long organizationId, @Param("projectId") Long projectId, @Param("fieldCode") String fieldCode);
}
