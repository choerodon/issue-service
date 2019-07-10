package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.infra.dto.IssueTypeSchemeConfigDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @date 2018/8/10
 */
@Component
public interface IssueTypeSchemeConfigMapper extends Mapper<IssueTypeSchemeConfigDTO> {
    void deleteBySchemeId(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);
}
