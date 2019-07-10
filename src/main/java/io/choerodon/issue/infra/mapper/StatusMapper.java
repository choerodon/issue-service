package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.api.vo.StatusSearchVO;
import io.choerodon.issue.infra.dto.Status;
import io.choerodon.issue.infra.dto.StatusWithInfo;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public interface StatusMapper extends Mapper<Status> {

    List<Long> selectStatusIds(@Param("organizationId") Long organizationId, @Param("statusSearchVO") StatusSearchVO statusSearchVO);

    List<StatusWithInfo> queryStatusList(@Param("organizationId") Long organizationId, @Param("statusIds") List<Long> statusIds);

    Status queryById(@Param("organizationId") Long organizationId, @Param("id") Long id);

    List<Status> batchStatusGet(@Param("ids") List<Long> ids);

    /**
     * 查询状态机下的所有状态
     *
     * @param organizationId
     * @param stateMachineIds
     * @return
     */
    List<Status> queryByStateMachineIds(@Param("organizationId") Long organizationId, @Param("stateMachineIds") List<Long> stateMachineIds);
}
