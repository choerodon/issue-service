package io.choerodon.issue.api.controller;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.issue.api.dto.IssueDTO;
import io.choerodon.issue.api.dto.IssueFieldValueDTO;
import io.choerodon.issue.api.dto.IssueTypeSchemeDTO;
import io.choerodon.issue.api.dto.SearchDTO;
import io.choerodon.issue.api.service.IssueFieldValueService;
import io.choerodon.issue.api.service.IssueService;
import io.choerodon.issue.api.service.IssueTypeSchemeService;
import io.choerodon.issue.infra.enums.PageSchemeLineType;
import io.choerodon.issue.infra.utils.VerifyUpdateUtil;
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
 * @date 2018/9/3
 */

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issue")
public class IssueController extends BaseController {

    @Autowired
    private IssueService issueService;
    @Autowired
    private IssueFieldValueService issueFieldValueService;
    @Autowired
    private VerifyUpdateUtil verifyUpdateUtil;
    @Autowired
    private IssueTypeSchemeService issueTypeSchemeService;

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "加载问题类型列表")
    @GetMapping(value = "/query_issue_type_scheme")
    public ResponseEntity<IssueTypeSchemeDTO> queryByIssueTypeScheme(@PathVariable("project_id") Long projectId) {
        return new ResponseEntity<>(issueTypeSchemeService.queryByProjectId(projectId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "创建问题时，根据问题类型id加载字段信息")
    @GetMapping(value = "/query_field/{issue_type_id}")
    public ResponseEntity<List<IssueFieldValueDTO>> queryByIssueType(@PathVariable("project_id") Long projectId, @PathVariable("issue_type_id") Long issueTypeId) {
        return new ResponseEntity<>(issueFieldValueService.queryByIssueTypeIdAndPageType(projectId, issueTypeId, PageSchemeLineType.CREATE.value()), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "根据id查询问题详情")
    @GetMapping(value = "/{id}")
    public ResponseEntity<IssueDTO> queryById(@PathVariable("project_id") Long projectId, @PathVariable("id") Long issueId) {
        return new ResponseEntity<>(issueService.queryById(projectId, issueId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "创建问题，issueDTO中的fieldValues只传fieldId和fieldValue")
    @PostMapping
    public ResponseEntity<IssueDTO> create(@PathVariable("project_id") Long projectId, @RequestBody @Valid IssueDTO issueDTO) {
        return new ResponseEntity<>(issueService.create(projectId, issueDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改问题")
    @PutMapping(value = "/{id}")
    public ResponseEntity<IssueDTO> update(@PathVariable("project_id") Long projectId, @PathVariable("id") Long issueId,
                                           @RequestBody JSONObject jsonObject) {
        IssueDTO issueDTO = new IssueDTO();
        List<String> updateFieldList = verifyUpdateUtil.verifyUpdateField(jsonObject, issueDTO);
        issueDTO.setId(issueId);
        issueDTO.setProjectId(projectId);
        return new ResponseEntity<>(issueService.updateIssue(issueDTO, updateFieldList), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "修改问题的自定义字段值，fieldValue只传id、fieldId、fieldValue和objectVersionNumber")
    @PutMapping(value = "/update_field_value/{id}")
    public ResponseEntity<Long> updateFieldValue(@PathVariable("project_id") Long projectId, @PathVariable("id") Long issueId,
                                                 @RequestBody @Valid IssueFieldValueDTO fieldValue) {
        return new ResponseEntity<>(issueFieldValueService.updateFieldValue(projectId, issueId, fieldValue), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "分页查询问题列表")
    @CustomPageRequest
    @PostMapping(value = "/pageIssue")
    public ResponseEntity<Page<IssueDTO>> pageQuery(@ApiIgnore
                                                    @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                    @PathVariable("project_id") Long projectId,
                                                    @RequestBody(required = false) SearchDTO searchDTO) {
        return new ResponseEntity<>(issueService.pageQuery(projectId, pageRequest, searchDTO), HttpStatus.OK);
    }
}
