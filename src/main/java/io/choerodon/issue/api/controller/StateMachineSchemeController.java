package io.choerodon.issue.api.controller;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.issue.api.dto.StateMachineSchemeConfigDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.api.service.StateMachineSchemeConfigService;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.api.validator.StateMachineSchemeValidator;
import io.choerodon.issue.infra.utils.ParamUtils;
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

import java.util.List;

@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/state_machine_scheme")
public class StateMachineSchemeController {

    @Autowired
    private StateMachineSchemeService schemeService;

    @Autowired
    private StateMachineSchemeValidator schemeValidator;

    @Autowired
    private StateMachineSchemeConfigService configService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询状态机方案列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<StateMachineSchemeDTO>> pagingQuery(@ApiIgnore
                                                                   @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                   @PathVariable("organization_id") Long organizationId,
                                                                   @RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) String description,
                                                                   @RequestParam(required = false) String[] param) {
        StateMachineSchemeDTO schemeDTO = new StateMachineSchemeDTO();
        schemeDTO.setOrganizationId(organizationId);
        schemeDTO.setName(name);
        schemeDTO.setDescription(description);
        return new ResponseEntity<>(schemeService.pageQuery(pageRequest, schemeDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建状态机方案")
    @PostMapping
    public ResponseEntity<StateMachineSchemeDTO> create(@PathVariable("organization_id") Long organizationId, @RequestBody StateMachineSchemeDTO schemeDTO) {
        schemeValidator.createValidate(schemeDTO);
        return new ResponseEntity<>(schemeService.create(organizationId, schemeDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新状态机方案")
    @PutMapping(value = "/{scheme_id}")
    public ResponseEntity<StateMachineSchemeDTO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("scheme_id") Long schemeId,
                                                        @RequestBody StateMachineSchemeDTO schemeDTO) {
        schemeValidator.updateValidate(schemeDTO);
        return new ResponseEntity<>(schemeService.update(organizationId, schemeId, schemeDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除状态机方案")
    @DeleteMapping(value = "/{scheme_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("scheme_id") Long schemeId) {
        return new ResponseEntity<>(schemeService.delete(organizationId, schemeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据id查询状态机方案对象")
    @GetMapping(value = "/{scheme_id}")
    public ResponseEntity<StateMachineSchemeDTO> getByStateId(@PathVariable("organization_id") Long organizationId, @PathVariable("scheme_id") Long schemeId) {
        return new ResponseEntity<>(schemeService.querySchemeWithConfigById(organizationId, schemeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建方案配置")
    @PostMapping(value = "/create_config/{scheme_id}/{state_machine_id}")
    public ResponseEntity<StateMachineSchemeDTO> createConfig(@PathVariable("organization_id") Long organizationId, @PathVariable("scheme_id") Long schemeId,
                                                              @PathVariable("state_machine_id") Long stateMachineId,
                                                              @RequestBody List<StateMachineSchemeConfigDTO> schemeDTOs) {
        return new ResponseEntity<>(configService.create(organizationId, schemeId, stateMachineId, schemeDTOs), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据状态机id删除方案配置")
    @DeleteMapping(value = "/delete_config/{scheme_id}/{state_machine_id}")
    public ResponseEntity<StateMachineSchemeDTO> deleteConfig(@PathVariable("organization_id") Long organizationId,
                                                              @PathVariable("scheme_id") Long schemeId,
                                                              @PathVariable("state_machine_id") Long stateMachineId) {
        return new ResponseEntity<>(configService.delete(organizationId, schemeId, stateMachineId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验状态机方案名字是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId, @RequestParam(value = "scheme_id", required = false) Long schemeId, @RequestParam("name") String name) {
        return new ResponseEntity<>(schemeService.checkName(organizationId, schemeId, name), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据状态机id查询所使用到该状态机的方案")
    @GetMapping(value = "/query_scheme/{state_machine_id}")
    public ResponseEntity<List<StateMachineSchemeDTO>> querySchemeByStateMachineId(@PathVariable("organization_id") Long organizationId, @PathVariable(value = "state_machine_id") Long stateMachineId) {
        return new ResponseEntity<>(schemeService.querySchemeByStateMachineId(organizationId, stateMachineId), HttpStatus.OK);
    }

}
