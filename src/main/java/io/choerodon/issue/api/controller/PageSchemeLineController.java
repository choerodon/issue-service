package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.PageSchemeLineDTO;
import io.choerodon.issue.api.service.PageSchemeLineService;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/8/23
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/page_scheme_line")
public class PageSchemeLineController extends BaseController {

    @Autowired
    private PageSchemeLineService pageSchemeLineService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据id查询页面方案配置")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PageSchemeLineDTO> queryById(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long pageSchemeLineId) {
        return new ResponseEntity<>(pageSchemeLineService.queryById(organizationId, pageSchemeLineId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建页面方案配置")
    @PostMapping
    public ResponseEntity<PageSchemeLineDTO> create(@PathVariable("organization_id") Long organizationId, @RequestBody @Valid PageSchemeLineDTO pageSchemeLineDTO) {
        return new ResponseEntity<>(pageSchemeLineService.create(organizationId, pageSchemeLineDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改页面方案配置")
    @PutMapping(value = "/{id}")
    public ResponseEntity<PageSchemeLineDTO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long pageSchemeLineId,
                                                    @RequestBody @Valid PageSchemeLineDTO pageSchemeLineDTO) {
        pageSchemeLineDTO.setId(pageSchemeLineId);
        pageSchemeLineDTO.setOrganizationId(organizationId);
        return new ResponseEntity<>(pageSchemeLineService.update(organizationId, pageSchemeLineDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验页面方案配置是否可以删除")
    @GetMapping(value = "/check_delete/{id}")
    public ResponseEntity<Map<String, Object>> checkDelete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long pageSchemeLineId) {
        return new ResponseEntity<>(pageSchemeLineService.checkDelete(organizationId, pageSchemeLineId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除页面方案配置")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long pageSchemeLineId) {
        return new ResponseEntity<>(pageSchemeLineService.delete(organizationId, pageSchemeLineId), HttpStatus.OK);
    }


}
