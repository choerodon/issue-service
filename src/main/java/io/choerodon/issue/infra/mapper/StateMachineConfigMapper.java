package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.infra.dto.StateMachineConfigDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public interface StateMachineConfigMapper extends Mapper<StateMachineConfigDTO> {
    StateMachineConfigDTO queryById(@Param("organizationId") Long organizationId, @Param("id") Long id);

    List<StateMachineConfigDTO> queryWithCodeInfo(@Param("organizationId") Long organizationId, @Param("transformId") Long transformId, @Param("type") String type);

    List<StateMachineConfigDTO> queryWithCodeInfoByTransformIds(@Param("organizationId") Long organizationId, @Param("type") String type, @Param("transformIds") List<Long> transformIds);
}
