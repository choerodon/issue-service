package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.FieldConfigSchemeDTO;
import io.choerodon.issue.api.dto.FieldConfigSchemeDetailDTO;
import io.choerodon.issue.api.service.FieldConfigSchemeService;
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

/**
 * @author jiameng.cao
 * @date 2018/8/27
 */

@RestController
@RequestMapping("/v1/organizations/{organization_id}/field_config_scheme")
public class FieldConfigSchemeController {
    @Autowired
    FieldConfigSchemeService fieldConfigSchemeService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询字段配置列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<FieldConfigSchemeDTO>> pageQuery(@ApiIgnore
                                                                @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                @PathVariable("organization_id") Long organizationId,
                                                                @RequestParam(required = false) String name,
                                                                @RequestParam(required = false) String[] param) {

        FieldConfigSchemeDTO fieldConfigSchemeDTO = new FieldConfigSchemeDTO();
        fieldConfigSchemeDTO.setOrganizationId(organizationId);
        fieldConfigSchemeDTO.setName(name);
        return new ResponseEntity<>(fieldConfigSchemeService.pageQuery(pageRequest, fieldConfigSchemeDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建字段配置方案")
    @PostMapping
    public ResponseEntity<FieldConfigSchemeDetailDTO> create(@PathVariable("organization_id") Long organizationId, @RequestBody @Valid FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO) {
        return new ResponseEntity<>(fieldConfigSchemeService.create(organizationId, fieldConfigSchemeDetailDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据id查询字段配置方案")
    @GetMapping(value = "/{id}")
    public ResponseEntity<FieldConfigSchemeDetailDTO> queryById(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long schemeId) {
        return new ResponseEntity<>(fieldConfigSchemeService.queryById(organizationId, schemeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改字段配置方案")
    @PutMapping(value = "/{id}")
    public ResponseEntity<FieldConfigSchemeDetailDTO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long schemeId,
                                                             @RequestBody @Valid FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO) {
        fieldConfigSchemeDetailDTO.setId(schemeId);
        fieldConfigSchemeDetailDTO.setOrganizationId(organizationId);
        return new ResponseEntity<>(fieldConfigSchemeService.update(organizationId, fieldConfigSchemeDetailDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除字段配置方案")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long schemeId) {
        return new ResponseEntity<>(fieldConfigSchemeService.delete(organizationId, schemeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验字段配置方案名称是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("name") String name,
                                             @RequestParam(value = "id", required = false) Long id) {
        return new ResponseEntity<>(fieldConfigSchemeService.checkName(organizationId, name, id), HttpStatus.OK);
    }

}
