package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.FieldDTO;
import io.choerodon.issue.api.dto.FieldDetailDTO;
import io.choerodon.issue.api.service.FieldService;
import io.choerodon.issue.infra.utils.ParamUtils;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

/**
 * @author jiameng.cao
 * @date 2018/8/21
 */
@RestController
@RequestMapping("/v1/organizations/{organization_id}/field")
public class FieldController {

    @Autowired
    private FieldService fieldService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询自定义字段列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<FieldDTO>> pageQuery(@ApiIgnore
                                                    @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                    @PathVariable("organization_id") Long organizationId,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String description,
                                                    @RequestParam(required = false) String[] param) {

        FieldDTO fieldDTO = new FieldDTO();
        fieldDTO.setOrganizationId(organizationId);
        fieldDTO.setName(name);
        fieldDTO.setDescription(description);
        return new ResponseEntity<>(fieldService.pageQuery(organizationId,pageRequest, fieldDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询自定义字段列表")
    @GetMapping(value = "/fields")
    public ResponseEntity<List<FieldDTO>> listQuery(@PathVariable("organization_id") Long organizationId,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String description,
                                                    @RequestParam(required = false) String[] param) {

        FieldDTO fieldDTO = new FieldDTO();
        fieldDTO.setName(name);
        fieldDTO.setDescription(description);
        return new ResponseEntity<>(fieldService.listQuery(organizationId, fieldDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建字段")
    @PostMapping
    public ResponseEntity<FieldDTO> create(@PathVariable("organization_id") Long organizationId, @RequestBody @Valid FieldDTO fieldDTO) {
        return new ResponseEntity<>(fieldService.create(organizationId, fieldDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改字段")
    @PutMapping(value = "/{id}")
    public ResponseEntity<FieldDetailDTO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long fieldId,
                                                 @RequestBody @Valid FieldDetailDTO fieldDTO) {
        fieldDTO.setId(fieldId);
        fieldDTO.setOrganizationId(organizationId);
        return new ResponseEntity<>(fieldService.update(fieldDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除字段")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long fieldId) {
        return new ResponseEntity<>(fieldService.delete(organizationId, fieldId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据id显示字段")
    @GetMapping(value = "/{id}")
    public ResponseEntity<FieldDetailDTO> queryById(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long fieldId) {
        return new ResponseEntity<>(fieldService.queryById(organizationId, fieldId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验字段名称是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("name") String name,
                                             @RequestParam(value = "id", required = false) Long id) {
        return new ResponseEntity<>(fieldService.checkName(organizationId, name, id), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新关联页面")
    @PostMapping(value = "/update_related_page")
    public ResponseEntity<List<Long>> updateRelatedPage(@PathVariable("organization_id") Long organizationId, @RequestParam("field_id") Long fieldId, @RequestBody List<Long> pageIds) {
        return new ResponseEntity<>(fieldService.updateRelatedPage(organizationId, fieldId, pageIds), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "获取关联页面")
    @GetMapping(value = "/query_related_page")
    public ResponseEntity<List<Long>> queryRelatedPage(@PathVariable("organization_id") Long organizationId, @RequestParam("field_id") Long fieldId) {
        return new ResponseEntity<>(fieldService.queryRelatedPage(organizationId, fieldId), HttpStatus.OK);
    }
}
