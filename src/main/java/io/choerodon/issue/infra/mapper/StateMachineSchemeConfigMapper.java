package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.StateMachineSchemeConfig;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public interface StateMachineSchemeConfigMapper extends BaseMapper<StateMachineSchemeConfig> {
    StateMachineSchemeConfig selectDefault(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);
}
