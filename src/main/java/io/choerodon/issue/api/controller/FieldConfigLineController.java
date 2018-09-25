package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.FieldConfigLineDTO;
import io.choerodon.issue.api.service.FieldConfigLineService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author jiameng.cao
 * @date 2018/8/23
 */

@RestController
@RequestMapping("/v1/organizations/{organization_id}/field_config_line")
public class FieldConfigLineController {
    @Autowired
    FieldConfigLineService fieldConfigLineService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改字段配置")
    @PutMapping(value = "/{id}")
    public ResponseEntity<FieldConfigLineDTO> updateFieldConfigLine(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long id,
                                                                    @RequestBody @Valid FieldConfigLineDTO fieldConfigLineDTO) {
        fieldConfigLineDTO.setId(id);
        return new ResponseEntity<>(fieldConfigLineService.update(fieldConfigLineDTO), HttpStatus.OK);
    }

}
