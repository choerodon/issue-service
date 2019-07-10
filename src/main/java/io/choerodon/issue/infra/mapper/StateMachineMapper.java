package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.api.vo.payload.StatusPayload;
import io.choerodon.issue.infra.dto.StateMachine;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public interface StateMachineMapper extends Mapper<StateMachine> {

    /**
     * 分页查询状态机
     *
     * @param stateMachine 状态机
     * @param param        模糊查询参数
     * @return 状态机列表
     */
    List<StateMachine> fulltextSearch(@Param("stateMachine") StateMachine stateMachine, @Param("param") String param);

    List<StateMachine> queryByIds(@Param("organizationId") Long organizationId, @Param("stateMachineIds") List<Long> stateMachineIds);

    StateMachine queryById(@Param("organizationId") Long organizationId, @Param("id") Long id);

    List<StatusPayload> getStatusBySmId(@Param("projectId") Long projectId, @Param("stateMachineId") Long stateMachineId);
}
