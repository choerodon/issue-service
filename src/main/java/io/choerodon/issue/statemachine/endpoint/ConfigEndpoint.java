package io.choerodon.issue.statemachine.endpoint;

import io.choerodon.issue.statemachine.bean.PropertyData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author shinan.chen
 * @date 2018/10/9
 */

@RestController
public class ConfigEndpoint {

    private PropertyData stateMachinePropertyData;

    public ConfigEndpoint(PropertyData stateMachinePropertyData) {
        this.stateMachinePropertyData = stateMachinePropertyData;
    }

    @GetMapping(value = "/statemachine/load_config_code", produces = {APPLICATION_JSON_VALUE})
    PropertyData loadConfigCode() {
        return stateMachinePropertyData;
    }

}
