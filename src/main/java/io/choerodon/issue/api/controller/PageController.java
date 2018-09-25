package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.PageDTO;
import io.choerodon.issue.api.dto.PageDetailDTO;
import io.choerodon.issue.api.service.PageService;
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

/**
 * @author shinan.chen
 * @date 2018/8/22
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/page")
public class PageController extends BaseController {

    @Autowired
    private PageService pageService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据id查询页面")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PageDetailDTO> queryById(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long pageId) {
        return new ResponseEntity<>(pageService.queryById(organizationId, pageId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建页面")
    @PostMapping
    public ResponseEntity<PageDetailDTO> create(@PathVariable("organization_id") Long organizationId, @RequestBody @Valid PageDetailDTO pageDTO) {
        return new ResponseEntity<>(pageService.create(organizationId, pageDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改页面")
    @PutMapping(value = "/{id}")
    public ResponseEntity<PageDetailDTO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long pageId,
                                                @RequestBody @Valid PageDetailDTO pageDTO) {
        pageDTO.setId(pageId);
        pageDTO.setOrganizationId(organizationId);
        return new ResponseEntity<>(pageService.update(organizationId, pageDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除页面")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long pageId) {
        return new ResponseEntity<>(pageService.delete(organizationId, pageId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询页面列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<PageDTO>> pageQuery(@ApiIgnore
                                                   @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                   @PathVariable("organization_id") Long organizationId,
                                                   @RequestParam(required = false) String name,
                                                   @RequestParam(required = false) String description,
                                                   @RequestParam(required = false) String[] param) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setOrganizationId(organizationId);
        pageDTO.setName(name);
        pageDTO.setDescription(description);
        return new ResponseEntity<>(pageService.pageQuery(organizationId,pageRequest, pageDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询页面列表")
    @GetMapping(value = "/pages")
    public ResponseEntity<List<PageDTO>> listQuery(@PathVariable("organization_id") Long organizationId,
                                                   @RequestParam(required = false) String name,
                                                   @RequestParam(required = false) String description,
                                                   @RequestParam(required = false) String[] param) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setOrganizationId(organizationId);
        pageDTO.setName(name);
        pageDTO.setDescription(description);
        return new ResponseEntity<>(pageService.listQuery(pageDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验页面名字是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("name") String name,
                                             @RequestParam(value = "id", required = false) Long id) {
        return new ResponseEntity<>(pageService.checkName(organizationId, name, id), HttpStatus.OK);
    }
}
