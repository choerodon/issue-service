package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.PriorityDTO;
import io.choerodon.issue.api.service.PriorityService;
import io.choerodon.issue.api.validator.PriorityValidator;
import io.choerodon.issue.infra.utils.ParamUtils;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cong.cheng
 * @date 2018/8/21
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/priority")
public class PriorityController {
    private static final String YES = "1";
    @Autowired
    private PriorityService priorityService;

    @Autowired
    private PriorityValidator priorityValidator;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "展示页面")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<List<PriorityDTO>> selectAll(@PathVariable("organization_id") Long organizationId,
                                                       @RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String description,
                                                       @RequestParam(required = false) String colour,
                                                       @RequestParam(required = false) String isDefault,
                                                       @RequestParam(required = false) String[] param) {
        PriorityDTO priorityDTO = new PriorityDTO();
        priorityDTO.setOrganizationId(organizationId);
        priorityDTO.setName(name);
        priorityDTO.setDescription(description);
        priorityDTO.setColour(colour);
        priorityDTO.setIsDefault(isDefault);
        return new ResponseEntity<>(priorityService.selectAll(priorityDTO,ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建优先级")
    @PostMapping
    public ResponseEntity<PriorityDTO> create(@PathVariable("organization_id") Long organizationId, @RequestBody PriorityDTO priorityDTO) {
        priorityValidator.createValidate(priorityDTO);
        return new ResponseEntity<>(priorityService.create(organizationId, priorityDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除优先级")
    @DeleteMapping(value = "/{priority_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("priority_id") Long priorityId) {
        return new ResponseEntity<>(priorityService.delete(organizationId, priorityId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新优先级")
    @PutMapping(value = "/{priority_id}")
    public ResponseEntity<PriorityDTO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("priority_id") Long priorityId,
                                              @RequestBody @Valid PriorityDTO priorityDTO) {
        priorityDTO.setId(priorityId);
        priorityDTO.setOrganizationId(organizationId);
        priorityValidator.updateValidate(priorityDTO);
        return new ResponseEntity<>(priorityService.update(priorityDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验优先级名字是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId, @RequestParam(value = "priority_id", required = false) Long priorityId, @RequestParam("name") String name) {
        return new ResponseEntity<>(priorityService.checkName(organizationId, priorityId, name), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新展示顺序")
    @PutMapping(value = "/sequence")
    public ResponseEntity<List<PriorityDTO>> updateByList(@PathVariable("organization_id") Long organizationId,
                                                          @RequestBody List<PriorityDTO> list) {

        return new ResponseEntity<>(priorityService.updateByList(list,organizationId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "优先级下拉列表（用于创建问题）")
    @GetMapping(value = "/list")
    public ResponseEntity<Map<String,Object>> selectByOrgId(@PathVariable("organization_id") Long organizationId) {
        Map<String,Object> map = new HashMap<>(2);
        PriorityDTO priorityDTO = new PriorityDTO();
        priorityDTO.setOrganizationId(organizationId);
        List<PriorityDTO> priorityDTOS = priorityService.selectAll(priorityDTO,null);
        map.put("list",priorityDTOS);
        map.put("defaultId", priorityDTOS.stream().filter(x->x.getIsDefault().equals(YES)).map(PriorityDTO::getId).findFirst().orElse(null));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
