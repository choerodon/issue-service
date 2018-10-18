package io.choerodon.issue.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.issue.api.dto.IssueDTO;
import io.choerodon.issue.domain.Issue;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.TransformInfo;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;


/**
 * @author peng.jiang@hand-china.com
 */
public interface StateMachineService {

    /**
     * 分页查询状态机
     *
     * @param organizationId 组织id
     * @param page           分页数
     * @param size           分页大小
     * @param sort           排序字段
     * @param name           名称
     * @param description    描述
     * @param param          模糊查询参数
     * @return 状态机列表
     */
    ResponseEntity<Page<StateMachineDTO>> pageQuery(Long organizationId, Integer page, Integer size, String[] sort, String name, String description, String[] param);

    /**
     * 删除状态机
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机id
     * @return
     */
    ResponseEntity<Boolean> delete(Long organizationId, Long stateMachineId);

    /**
     * 删除校验
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机id
     * @return
     */
    Map<String, Object> checkDelete(Long organizationId, Long stateMachineId);

    /**
     * 显示事件单转换
     *
     * @param organizationId
     * @param currentStateId
     * @return
     */
    ResponseEntity<List<TransformInfo>> transfList(Long organizationId, Long projectId, Long issueId);

    /**
     * 状态机执行转换
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机Id
     * @param transfId       转换Id
     * @param currentStateId 当前状态Id
     * @param issueId        事件单id
     * @return
     */
    ResponseEntity<ExecuteResult> doTransf(Long organizationId, Long projectId, Long issueId, Long transfId);

    /**
     * 创建问题和状态机实例
     * @param organizationId
     * @param stateMachineId
     * @return
     */
    Issue createIssue(Long organizationId, Long stateMachineId);
}
