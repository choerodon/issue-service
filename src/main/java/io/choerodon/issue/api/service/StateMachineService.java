package io.choerodon.issue.api.service;

import io.choerodon.core.domain.Page;
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
}
