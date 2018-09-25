package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public interface StateMachineSchemeMapper extends BaseMapper<StateMachineScheme> {

    /**
     * 获取状态机方案及其配置
     * @param schemeId 状态机方案id
     * @return 状态机方案及配置
     */
    StateMachineScheme getSchemeWithConfigById(Long schemeId);

    /**
     * 分页查询状态方案
     *
     * @param scheme 状态机方案
     * @param param 模糊查询参数
     * @return 方案列表
     */
    List<StateMachineScheme> fulltextSearch(@Param("scheme") StateMachineScheme scheme, @Param("param") String param);

    /**
     * 根据状态机id查询所使用到该状态机的方案
     * @param organizationId 组织id
     * @param stateMachineId 状态机id
     * @return 方案列表
     */
    List<StateMachineScheme> querySchemeByStateMachineId(@Param("organizationId") Long organizationId, @Param("stateMachineId") Long stateMachineId);
}
