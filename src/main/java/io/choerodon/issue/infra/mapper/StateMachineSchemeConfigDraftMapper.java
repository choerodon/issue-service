package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.StateMachineSchemeConfigDraft;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @since 2018/11/19
 */
@Component
public interface StateMachineSchemeConfigDraftMapper extends Mapper<StateMachineSchemeConfigDraft> {
    StateMachineSchemeConfigDraft selectDefault(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);
}
