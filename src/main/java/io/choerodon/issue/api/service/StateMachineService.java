package io.choerodon.issue.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.issue.api.dto.Status;
import io.choerodon.issue.api.dto.payload.RemoveStatusWithProject;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
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
     * 删除节点校验
     *
     * @param organizationId
     * @param stateMachineId
     * @return
     */
    Map<String, Object> checkDeleteNode(Long organizationId, Long stateMachineId, Long statusId);

    /**
     * 使状态机变成非活跃状态
     *
     * @param organizationId
     * @param stateMachineIds
     */
    void notActiveStateMachine(Long organizationId, List<Long> stateMachineIds);

    /**
     * 处理删除状态机的某几个状态时，关联的哪几个项目哪几个状态可以删除
     *
     * @param organizationId
     * @param stateMachineId
     * @param deleteStatusIds
     */
    List<RemoveStatusWithProject> handleRemoveStatusByStateMachineId(Long organizationId, Long stateMachineId, List<Long> deleteStatusIds);

    /**
     * 处理删除方案的某几个状态时，关联的哪几个项目哪几个状态可以删除
     *
     * @param organizationId
     * @param schemeIds
     * @param deleteStatusIds
     * @return
     */
    List<RemoveStatusWithProject> handleRemoveStatusBySchemeIds(Long organizationId, List<Long> schemeIds, List<Long> deleteStatusIds);
}
