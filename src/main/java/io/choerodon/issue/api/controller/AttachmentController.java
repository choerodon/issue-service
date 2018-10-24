package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.AttachmentDTO;
import io.choerodon.issue.api.service.AttachmentService;
import io.choerodon.issue.infra.utils.ParamUtils;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * @author jiameng.cao
 * @date 2018/9/4
 */

@RestController
@RequestMapping("v1/projects/{project_id}/attachment")
public class AttachmentController {
    @Autowired
    AttachmentService attachmentService;

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "获取附件列表")
    @GetMapping(value = "/attachment_list")
    public ResponseEntity<List<AttachmentDTO>> listQuery(@PathVariable("project_id") Long projectId,
                                                         @RequestParam(required = false) String[] param) {

        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setProjectId(projectId);

        return new ResponseEntity<>(attachmentService.listQuery(attachmentDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "获取问题关联的附件")
    @GetMapping(value = "/attachment_by_issue")
    public ResponseEntity<List<AttachmentDTO>> queryByIssue(@PathVariable("project_id") Long projectId,
                                                            @RequestParam(value = "issue_id") Long issueId) {
        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setProjectId(projectId);
        return new ResponseEntity<>(attachmentService.queryByIssue(attachmentDTO, issueId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "删除附件")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("project_id") Long projectId, @PathVariable("id") Long attachmentId) {
        return new ResponseEntity<>(attachmentService.delete(projectId, attachmentId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("上传附件")
    @PostMapping
    public ResponseEntity<List<AttachmentDTO>> uploadAttachment(@PathVariable(name = "project_id") Long projectId,
                                                                @RequestBody @Valid AttachmentDTO attachmentDTO,
                                                                HttpServletRequest request) {
        return Optional.ofNullable(attachmentService.create(projectId, attachmentDTO, request))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.attachment.upload"));
    }


    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("上传附件，直接返回地址")
    @PostMapping(value = "/upload_for_address")
    public ResponseEntity<List<String>> uploadForAddress(@ApiParam(value = "project_id", required = true)
                                                         @PathVariable(name = "project_id") Long projectId,
                                                         HttpServletRequest request) {
        return Optional.ofNullable(attachmentService.uploadForAddress(projectId, request))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.attachment.upload"));
    }
}
