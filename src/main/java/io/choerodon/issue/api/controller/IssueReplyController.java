package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.IssueReplyDTO;
import io.choerodon.issue.api.service.IssueReplyService;
import io.choerodon.issue.infra.utils.ParamUtils;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author jiameng.cao
 * @date 2018/9/3
 */

@RestController
@RequestMapping("v1/projects/{project_id}/issue_reply")
public class IssueReplyController {
    @Autowired
    IssueReplyService issueReplyService;

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "获取事件单回复列表")
    @GetMapping(value = "/issue_reply_list")
    public ResponseEntity<List<IssueReplyDTO>> listQuery(@PathVariable("project_id") Long projectId,
                                                         @RequestParam(required = false) String[] param) {

        IssueReplyDTO issueReplyDTO = new IssueReplyDTO();
        issueReplyDTO.setProjectId(projectId);

        return new ResponseEntity<>(issueReplyService.listQuery(issueReplyDTO, ParamUtils.arrToStr(param)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "创建事件单回复")
    @PostMapping
    public ResponseEntity<IssueReplyDTO> create(@PathVariable("project_id") Long projectId, @RequestBody @Valid IssueReplyDTO issueReplyDTO) {
        return new ResponseEntity<>(issueReplyService.create(projectId, issueReplyDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "修改事件单回复")
    @PutMapping(value = "/{id}")
    public ResponseEntity<IssueReplyDTO> update(@PathVariable("project_id") Long projectId, @PathVariable("id") Long issueReplyId,
                                                @RequestBody @Valid IssueReplyDTO issueReplyDTO) {
        issueReplyDTO.setId(issueReplyId);
        issueReplyDTO.setProjectId(projectId);
        return new ResponseEntity<>(issueReplyService.update(issueReplyDTO), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "删除事件单回复")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("project_id") Long projectId, @PathVariable("id") Long issueReplyId) {
        return new ResponseEntity<>(issueReplyService.delete(projectId, issueReplyId), HttpStatus.OK);
    }
}
