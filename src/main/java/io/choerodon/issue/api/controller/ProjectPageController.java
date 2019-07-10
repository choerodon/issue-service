package io.choerodon.issue.api.controller;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.issue.api.dto.PageDTO;
import io.choerodon.issue.api.dto.PageSearchDTO;
import io.choerodon.issue.api.service.PageService;
import io.choerodon.mybatis.annotation.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author shinan.chen
 * @since 2019/4/4
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/page")
public class ProjectPageController extends BaseController {
    @Autowired
    private PageService pageService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "分页查询页面列表")
    @CustomPageRequest
    @PostMapping
    public ResponseEntity<PageInfo<PageDTO>> pageQuery(@ApiIgnore
                                                       @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                       @ApiParam(value = "项目id", required = true)
                                                       @PathVariable("project_id") Long projectId,
                                                       @ApiParam(value = "组织id", required = true)
                                                       @RequestParam Long organizationId,
                                                       @ApiParam(value = "search dto", required = true)
                                                       @RequestBody(required = false) PageSearchDTO searchDTO) {
        return new ResponseEntity<>(pageService.pageQuery(organizationId, pageRequest, searchDTO), HttpStatus.OK);
    }
}
