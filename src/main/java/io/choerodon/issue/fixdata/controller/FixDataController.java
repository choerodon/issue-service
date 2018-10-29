package io.choerodon.issue.fixdata.controller;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.dto.Status;
import io.choerodon.issue.api.service.FixDataService;
import io.choerodon.issue.fixdata.dto.StatusForMoveDataDO;
import io.choerodon.issue.infra.feign.dto.StatusDTO;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 敏捷修复数据专用
 *
 * @author shinan.chen
 * @date 2018/10/25
 */

@RestController
@RequestMapping(value = "/v1/organizations/fix_data")
public class FixDataController extends BaseController {

    @Autowired
    private FixDataService fixDataService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "通过敏捷状态数据，修复状态、状态机、状态机方案数据")
    @PostMapping(value = "/state_machine_scheme")
    public ResponseEntity<Map<Long, List<Status>>> fixStateMachineScheme(@ApiParam(value = "敏捷状态数据", required = true)
                                                                                @RequestBody List<StatusForMoveDataDO> statusForMoveDataDOList) {
        return new ResponseEntity<>(fixDataService.fixStateMachineScheme(statusForMoveDataDOList), HttpStatus.OK);
    }

}
