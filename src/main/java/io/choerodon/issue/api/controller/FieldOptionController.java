package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.FieldOptionDTO;
import io.choerodon.issue.api.service.FieldOptionService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @date 2018/8/21
 */

@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/field_option")
public class FieldOptionController {
    @Autowired
    private FieldOptionService fieldOptionService;


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验字段值是否可以删除")
    @GetMapping(value = "/check_delete/{id}")
    public ResponseEntity<Map<String, Object>> checkDelete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long fieldOptionId, @RequestParam("fieldId") Long fieldId) {
        return new ResponseEntity<>(fieldOptionService.checkDelete(fieldId, fieldOptionId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据字段id查询字段选项列表")
    @GetMapping(value = "/{field_id}")
    public ResponseEntity<List<FieldOptionDTO>> queryByFieldId(@PathVariable("organization_id") Long organizationId, @PathVariable("field_id") Long fieldId) {
        return new ResponseEntity<>(fieldOptionService.queryByFieldId(organizationId, fieldId), HttpStatus.OK);
    }
}
