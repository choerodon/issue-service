package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.api.dto.ObjectSchemeSearchDTO;
import io.choerodon.issue.domain.ObjectScheme;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
public interface ObjectSchemeMapper extends Mapper<ObjectScheme> {
    /**
     * 分页查询对象方案
     *
     * @param organizationId
     * @param searchDTO
     * @return
     */
    List<ObjectScheme> fulltextSearch(@Param("organizationId") Long organizationId, @Param("searchDTO") ObjectSchemeSearchDTO searchDTO);
}
