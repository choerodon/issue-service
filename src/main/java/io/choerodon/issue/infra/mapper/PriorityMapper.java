package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.Priority;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author cong.cheng
 * @Date 2018/8/21
 */
@Component
public interface PriorityMapper extends BaseMapper<Priority> {
    /**
     * 查询优先级表
     *
     * @param priority 精确查询的字段包装
     * @param param    模糊查询参数
     * @return 优先级列表
     */
    List<Priority> fulltextSearch(@Param("priority") Priority priority, @Param("param") String param);

    /**
     * 得到下一个顺序号
     *
     * @param organizationId 组织id
     * @return 顺序号
     */
    BigDecimal getNextSequence(@Param("organizationId") Long organizationId);

    /**
     * 根据id更新优先级的顺序
     *
     * @param priority 优先级对象
     * @return 更新是否成功
     */
    int updateSequenceById(@Param("priority") Priority priority);

    /**
     * 取消默认优先级
     *
     * @param organizationId 组织id
     */
    void cancelDefaultPriority(@Param("organizationId") Long organizationId);

    /**
     * 更新最小的id为默认优先级
     * @param organizationId
     */
    void updateMinIdAsDefault(@Param("organizationId") Long organizationId);

    /**
     * @param organizationId organizationId
     * @return int
     */
    int selectDefaultCount(@Param("organizationId") Long organizationId);
}
