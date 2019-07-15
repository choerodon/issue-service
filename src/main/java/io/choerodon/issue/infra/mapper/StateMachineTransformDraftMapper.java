package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.infra.dto.StateMachineTransformDraftDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
public interface StateMachineTransformDraftMapper extends Mapper<StateMachineTransformDraftDTO> {

    /**
     * 删除节点时，删除关联的转换
     *
     * @param nodeId 节点id
     * @return
     */
    int deleteByNodeId(Long nodeId);

    StateMachineTransformDraftDTO queryById(@Param("organizationId") Long organizationId, @Param("id") Long id);
}
