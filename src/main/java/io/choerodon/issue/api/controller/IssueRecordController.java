package io.choerodon.issue.api.controller;

import io.choerodon.issue.api.dto.IssueRecordViewDTO;
import io.choerodon.issue.api.service.IssueRecordService;
import io.choerodon.issue.domain.IssueRecord;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/projects/{project_id}/issue_record")
public class IssueRecordController {
    //todo 测试接口，正式发布时删除这个controller

    @Autowired
    private IssueRecordService issueRecordService;

    @GetMapping("/{issue_id}")
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "获取问题记录详情")
    public ResponseEntity<List<IssueRecordViewDTO>> queryByIssueId(@PathVariable("project_id") Long projectId, @PathVariable("issue_id") Long issueId) {
        return new ResponseEntity<>(issueRecordService.queryByIssueId(projectId, issueId), HttpStatus.OK);
    }

    @PostMapping("/create/{project_id}/{issue_id}")
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "创建记录")
    public ResponseEntity<List<IssueRecord>> create(@PathVariable("project_id") Long projectId, @PathVariable("issue_id") Long issueId, @RequestBody IssueRecord issueRecord) {
        return new ResponseEntity<>(issueRecordService.create(projectId, issueId, issueRecord), HttpStatus.OK);
    }

    @PostMapping("/create_list/{project_id}/{issue_id}")
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "创建记录列表")
    public ResponseEntity<List<IssueRecord>> createList(@PathVariable("project_id") Long projectId, @PathVariable("issue_id") Long issueId, @RequestBody List<IssueRecord> issueRecords) {
        return new ResponseEntity<>(issueRecordService.create(projectId, issueId, issueRecords), HttpStatus.OK);
    }
}
