package io.choerodon.issue.api.service;

import io.choerodon.issue.fixdata.dto.StatusForMoveDataDO;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/10/25
 */
public interface FixDataService {
    /**
     * 修复状态、状态机、状态机方案数据
     *
     * @param statusForMoveDataDOList
     * @return
     */
    void fixStateMachineScheme(List<StatusForMoveDataDO> statusForMoveDataDOList);

    /**
     * 获取所有组织的优先级
     *
     * @return
     */
    Map<Long, Map<String, Long>> queryPriorities();

    /**
     * 获取所有组织的问题类型
     *
     * @return
     */
    Map<Long, Map<String, Long>> queryIssueTypes();
}
