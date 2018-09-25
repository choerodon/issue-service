package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.FieldConfigDTO;
import io.choerodon.issue.api.dto.FieldConfigDetailDTO;
import io.choerodon.issue.api.service.FieldConfigLineService;
import io.choerodon.issue.api.service.FieldConfigService;
import io.choerodon.issue.infra.utils.ParamUtils;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
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
import java.util.Optional;


/**
 * @author jiameng.cao
 * @date 2018/8/23
 */
@RestController
@RequestMapping("/v1/organizations/{organization_id}/field_config")
public class FieldConfigController {
    @Autowired
    FieldConfigService fieldConfigService;

    @Autowired
    FieldConfigLineService fieldConfigLineService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询字段配置列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<FieldConfigDTO>> pageQuery(@ApiIgnore
                                                          @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                          @PathVariable("organization_id") Long organizationId,
                                                          @RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String description,
                                                          @RequestParam(required = false) String[] param) {

        FieldConfigDTO fieldConfigDTO = new FieldConfigDTO();
        fieldConfigDTO.setOrganizationId(organizationId);
        fieldConfigDTO.setName(name);
        fieldConfigDTO.setDescription(description);
        return new ResponseEntity<>(fieldConfigService.pageQuery(organizationId, pageRequest, fieldConfigDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建字段配置")
    @PostMapping
    public ResponseEntity<FieldConfigDTO> create(@PathVariable("organization_id") Long organizationId, @RequestBody @Valid FieldConfigDTO fieldConfigDTO) {
        return new ResponseEntity<>(fieldConfigService.create(organizationId, fieldConfigDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除字段配置")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long id) {
        return new ResponseEntity<>(fieldConfigService.delete(organizationId, id), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "编辑字段配置")
    @PutMapping(value = "/{id}")
    public ResponseEntity<FieldConfigDetailDTO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long fieldConfigId,
                                                       @RequestBody @Valid FieldConfigDetailDTO fieldConfigDetailDTO) {
        fieldConfigDetailDTO.setId(fieldConfigId);
        fieldConfigDetailDTO.setOrganizationId(organizationId);
        return new ResponseEntity<>(fieldConfigService.update(fieldConfigDetailDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据id查询字段配置")
    @GetMapping(value = "/{id}")
    public ResponseEntity<FieldConfigDetailDTO> queryById(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long fieldConfigId) {
        return new ResponseEntity<>(fieldConfigService.queryByFieldConfigId(organizationId, fieldConfigId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("获取字段配置列表")
    @GetMapping(value = "/configs")
    public ResponseEntity<List<FieldConfigDTO>> queryByOrgId(@PathVariable("organization_id") Long organizationId) {
        return Optional.ofNullable(fieldConfigService.queryByOrgId(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.fieldConfig.queryByOrgId"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验字段配置名称是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("name") String name,
                                             @RequestParam(value = "id", required = false) Long id) {
        return new ResponseEntity<>(fieldConfigService.checkName(organizationId, name, id), HttpStatus.OK);
    }
}
