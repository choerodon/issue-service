package io.choerodon.issue.api.controller;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.service.IssueTypeService;
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

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author shinan.chen
 * @date 2018/8/8
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/issue_type")
public class IssueTypeController extends BaseController {

    @Autowired
    private IssueTypeService issueTypeService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据id查询问题类型")
    @GetMapping(value = "/{id}")
    public ResponseEntity<IssueTypeDTO> queryIssueTypeById(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeId) {
        return new ResponseEntity<>(issueTypeService.queryById(organizationId, issueTypeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建问题类型")
    @PostMapping
    public ResponseEntity<IssueTypeDTO> create(@PathVariable("organization_id") Long organizationId, @RequestBody @Valid IssueTypeDTO issueTypeDTO) {
        return new ResponseEntity<>(issueTypeService.create(organizationId, issueTypeDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改问题类型")
    @PutMapping(value = "/{id}")
    public ResponseEntity<IssueTypeDTO> update(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeId,
                                               @RequestBody @Valid IssueTypeDTO issueTypeDTO) {
        issueTypeDTO.setId(issueTypeId);
        issueTypeDTO.setOrganizationId(organizationId);
        return new ResponseEntity<>(issueTypeService.update(issueTypeDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除问题类型")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeId) {
        return new ResponseEntity<>(issueTypeService.delete(organizationId, issueTypeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询问题类型列表")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<IssueTypeDTO>> pageQuery(@ApiIgnore
                                                        @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                        @PathVariable("organization_id") Long organizationId,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String description,
                                                        @RequestParam(required = false) String[] param) {
        IssueTypeDTO issueTypeDTO = new IssueTypeDTO();
        issueTypeDTO.setOrganizationId(organizationId);
        issueTypeDTO.setName(name);
        issueTypeDTO.setDescription(description);
        return new ResponseEntity<>(issueTypeService.pageQuery(pageRequest, issueTypeDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验问题类型是否可以删除")
    @GetMapping(value = "/check_delete/{id}")
    public ResponseEntity<Map<String, Object>> checkDelete(@PathVariable("organization_id") Long organizationId, @PathVariable("id") Long issueTypeId) {
        return new ResponseEntity<>(issueTypeService.checkDelete(organizationId, issueTypeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验问题类型名字是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("name") String name,
                                             @RequestParam(value = "id", required = false) Long id) {
        return new ResponseEntity<>(issueTypeService.checkName(organizationId, name, id), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("获取问题类型列表")
    @GetMapping(value = "/types")
    public ResponseEntity<List<IssueTypeDTO>> queryByOrgId(@PathVariable("organization_id") Long organizationId) {
        return Optional.ofNullable(issueTypeService.queryByOrgId(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.issue.queryByOrgId"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询所有问题类型及关联的方案")
    @GetMapping(value = "/query_issue_type/{scheme_id}")
    public ResponseEntity<List<IssueTypeDTO>> queryIssueType(@PathVariable("organization_id") Long organizationId, @PathVariable("scheme_id") Long schemeId) {
        return new ResponseEntity<>(issueTypeService.queryIssueType(organizationId, schemeId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据组织id查询类型，map")
    @GetMapping(value = "/type_map")
    public ResponseEntity<Map<Long, IssueTypeDTO>> listIssueTypeMap(@PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(issueTypeService.listIssueTypeMap(organizationId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "迁移组织层问题类型数据")
    @PostMapping(value = "/init_data")
    public ResponseEntity<Map<Long, Map<String, Long>>> initIssueTypeData(@PathVariable("organization_id") Long organizationId,
                                                                          @RequestBody List<Long> orgIds) {
        return new ResponseEntity<>(issueTypeService.initIssueTypeData(organizationId, orgIds), HttpStatus.OK);
    }

}
