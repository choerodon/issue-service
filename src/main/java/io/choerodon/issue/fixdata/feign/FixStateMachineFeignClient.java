package io.choerodon.issue.fixdata.feign;

import io.choerodon.issue.api.dto.Status;
import io.choerodon.issue.fixdata.dto.StatusForMoveDataDO;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.StatusDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/10/25
 */
@FeignClient(value = "state-machine-service", fallback = FixStateMachineFeignClientFallback.class)
@Component
public interface FixStateMachineFeignClient {

    /**
     * 修复创建项目默认状态机
     *
     * @param organizationId
     * @param projectCode
     * @param statuses
     * @return
     */
    @RequestMapping(value = "/v1/fix_data/create_state_machine", method = RequestMethod.POST)
    ResponseEntity<Long> createStateMachine(@RequestParam(value = "organization_id") Long organizationId,
                                                       @RequestParam(value = "project_code") String projectCode,
                                                       @RequestBody List<String> statuses);

    /**
     * 根据敏捷状态数据，修复创建状态
     * @param statusForMoveDataDOList
     * @return
     */
    @RequestMapping(value = "/v1/fix_data/create_status", method = RequestMethod.POST)
    void createStatus(@RequestBody List<StatusForMoveDataDO> statusForMoveDataDOList);

}
