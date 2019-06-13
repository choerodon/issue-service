package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.StateMachineSchemeConfig;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public interface StateMachineSchemeConfigMapper extends Mapper<StateMachineSchemeConfig> {
    StateMachineSchemeConfig selectDefault(@Param("organizationId") Long organizationId, @Param("schemeId") Long schemeId);

    List<StateMachineSchemeConfig> queryByStateMachineIds(@Param("organizationId") Long organizationId, @Param("stateMachineIds") List<Long> stateMachineIds);

    List<StateMachineSchemeConfig> queryByOrgId(@Param("organizationId") Long organizationId);
}
