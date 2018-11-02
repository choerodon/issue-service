package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.PageIssueTypeSchemeDTO;
import io.choerodon.issue.api.service.PageIssueSchemeService;
import io.choerodon.issue.api.validator.PageIssueSchemeLineValidator;
import io.choerodon.issue.api.validator.PageIssueSchemeValidator;
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

@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/page_issue")
public class PageIssueSchemeController {

    @Autowired
    private PageIssueSchemeService schemeService;

    @Autowired
    private PageIssueSchemeValidator schemeValidator;

    @Autowired
    private PageIssueSchemeLineValidator lineValidator;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询问题类型页面方案列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<PageIssueTypeSchemeDTO>> pagingQuery(@ApiIgnore
                                                                @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                    @PathVariable("organization_id") Long organizationId,
                                                                    @RequestParam(required = false) String name,
                                                                    @RequestParam(required = false) String description,
                                                                    @RequestParam(required = false) String[] param) {
        PageIssueTypeSchemeDTO schemeDTO = new PageIssueTypeSchemeDTO();
        schemeDTO.setOrganizationId(organizationId);
        schemeDTO.setName(name);
        schemeDTO.setDescription(description);
        return new ResponseEntity<>(schemeService.pageQuery(pageRequest, schemeDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建问题类型页面方案")
    @PostMapping
    public ResponseEntity<PageIssueTypeSchemeDTO> create(@PathVariable("organization_id") Long organizationId, @RequestBody PageIssueTypeSchemeDTO schemeDTO) {
        schemeValidator.createValidate(schemeDTO);
        return new ResponseEntity<>(schemeService.create(organizationId, schemeDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新问题类型页面方案")
    @PutMapping(value = "/{scheme_id}")
    public ResponseEntity<PageIssueTypeSchemeDTO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("scheme_id") Long schemeId,
                                                         @RequestBody PageIssueTypeSchemeDTO schemeDTO) {
        schemeValidator.updateValidate(schemeDTO);
        return new ResponseEntity<>(schemeService.update(organizationId, schemeId, schemeDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除问题类型页面方案")
    @DeleteMapping(value = "/{scheme_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("scheme_id") Long schemeId) {
        return new ResponseEntity<>(schemeService.delete(organizationId, schemeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据id查询问题类型页面方案对象")
    @GetMapping(value = "/{scheme_id}")
    public ResponseEntity<PageIssueTypeSchemeDTO> querySchemeWithConfigById(@PathVariable("organization_id") Long organizationId, @PathVariable("scheme_id") Long schemeId) {
        return new ResponseEntity<>(schemeService.querySchemeWithConfigById(organizationId, schemeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验问题类型页面方案名字是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId, @RequestParam(value = "scheme_id", required = false) Long schemeId, @RequestParam("name") String name) {
        return new ResponseEntity<>(schemeService.checkName(organizationId, schemeId, name), HttpStatus.OK);
    }

}
