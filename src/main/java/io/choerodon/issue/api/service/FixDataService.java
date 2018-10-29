package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.Status;
import io.choerodon.issue.fixdata.dto.StatusForMoveDataDO;
import io.choerodon.issue.infra.feign.dto.StatusDTO;

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
    Map<Long, List<Status>> fixStateMachineScheme(List<StatusForMoveDataDO> statusForMoveDataDOList);
}
