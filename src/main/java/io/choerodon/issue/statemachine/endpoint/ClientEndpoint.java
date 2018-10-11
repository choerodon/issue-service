package io.choerodon.issue.statemachine.endpoint;

import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.TransformInfo;
import io.choerodon.issue.statemachine.bean.PropertyData;
import io.choerodon.issue.statemachine.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author shinan.chen
 * @date 2018/10/9
 */

@RestController
public class ClientEndpoint {

    @Autowired
    private ClientService clientService;

    private PropertyData stateMachinePropertyData;

    public ClientEndpoint(PropertyData stateMachinePropertyData) {
        this.stateMachinePropertyData = stateMachinePropertyData;
    }

    @GetMapping(value = "/statemachine/load_config_code", produces = {APPLICATION_JSON_VALUE})
    public PropertyData loadConfigCode() {
        return stateMachinePropertyData;
    }

    /**
     * 执行条件，验证，后置处理
     *
     * @param instanceId
     * @param targetStatusId
     * @param type
     * @param conditionStrategy
     * @param configDTOS
     * @return
     */
    @PostMapping(value = "v1/statemachine/execute_config")
    public ResponseEntity<ExecuteResult> executeConfig(@RequestParam(value = "instance_id") Long instanceId,
                                                       @RequestParam(value = "target_status_id", required = false) Long targetStatusId,
                                                       @RequestParam(value = "type") String type,
                                                       @RequestParam(value = "condition_strategy", required = false) String conditionStrategy,
                                                       @RequestBody List<StateMachineConfigDTO> configDTOS) {
        return new ResponseEntity<>(clientService.configExecute(instanceId, targetStatusId, type,
                conditionStrategy, configDTOS), HttpStatus.OK);
    }

    /**
     * 根据条件过滤转换
     *
     * @param instanceId
     * @param transformInfos
     * @return
     */
    @PostMapping(value = "v1/statemachine/filter_transform")
    public ResponseEntity<List<TransformInfo>> filterTransform(@RequestParam(value = "instance_id") Long instanceId,
                                                               @RequestBody List<TransformInfo> transformInfos) {
        return new ResponseEntity<>(clientService.conditionFilter(instanceId, transformInfos), HttpStatus.OK);
    }

}
