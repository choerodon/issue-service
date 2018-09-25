package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.PageSchemeDTO;
import io.choerodon.issue.api.dto.PageSchemeDetailDTO;
import io.choerodon.issue.api.service.PageSchemeService;
import io.choerodon.issue.infra.utils.ParamUtils;
import io.choerodon.core.base.BaseController;
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
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/8/23
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/page_scheme")
public class PageSchemeController extends BaseController {

    @Autowired
    private PageSchemeService pageSchemeService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据id查询页面方案")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PageSchemeDetailDTO> queryById(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long pageSchemeId) {
        return new ResponseEntity<>(pageSchemeService.queryById(organizationId, pageSchemeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建页面方案")
    @PostMapping
    public ResponseEntity<PageSchemeDetailDTO> create(@PathVariable("organization_id") Long organizationId, @RequestBody @Valid PageSchemeDetailDTO pageSchemeDTO) {
        return new ResponseEntity<>(pageSchemeService.create(organizationId, pageSchemeDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改页面方案")
    @PutMapping(value = "/{id}")
    public ResponseEntity<PageSchemeDetailDTO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long pageSchemeId,
                                                      @RequestBody @Valid PageSchemeDetailDTO pageSchemeDTO) {
        pageSchemeDTO.setId(pageSchemeId);
        pageSchemeDTO.setOrganizationId(organizationId);
        return new ResponseEntity<>(pageSchemeService.update(organizationId, pageSchemeDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验页面方案是否可以删除")
    @GetMapping(value = "/check_delete/{id}")
    public ResponseEntity<Map<String, Object>> checkDelete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long pageSchemeId) {
        return new ResponseEntity<>(pageSchemeService.checkDelete(organizationId, pageSchemeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除页面方案")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long pageSchemeId) {
        return new ResponseEntity<>(pageSchemeService.delete(organizationId, pageSchemeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询页面方案列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<PageSchemeDTO>> pageQuery(@ApiIgnore
                                                         @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                         @PathVariable("organization_id") Long organizationId,
                                                         @RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String description,
                                                         @RequestParam(required = false) String[] param) {
        PageSchemeDTO pageSchemeDTO = new PageSchemeDTO();
        pageSchemeDTO.setOrganizationId(organizationId);
        pageSchemeDTO.setName(name);
        pageSchemeDTO.setDescription(description);
        return new ResponseEntity<>(pageSchemeService.pageQuery(organizationId, pageRequest, pageSchemeDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验页面方案名字是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("name") String name,
                                             @RequestParam(value = "id", required = false) Long id) {
        return new ResponseEntity<>(pageSchemeService.checkName(organizationId, name, id), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询所有页面方案")
    @GetMapping(value = "/query_all")
    public ResponseEntity<List<PageSchemeDetailDTO>> queryAll(@PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(pageSchemeService.queryAll(organizationId), HttpStatus.OK);
    }

}
