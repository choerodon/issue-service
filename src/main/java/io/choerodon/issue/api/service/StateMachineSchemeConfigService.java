package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.StateMachineSchemeConfigDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.domain.StateMachineSchemeConfig;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;

/**
 * @author shinan.chen
 * @Date 2018/8/2
 */
public interface StateMachineSchemeConfigService extends BaseService<StateMachineSchemeConfig> {

    /**
     * 根据状态机id删除配置
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机id
     * @return
     */
    StateMachineSchemeDTO delete(Long organizationId, Long schemeId, Long stateMachineId);

    /**
     * 创建方案
     *
     * @param organizationId
     * @param schemeId
     * @param schemeDTOs
     * @return
     */
    StateMachineSchemeDTO create(Long organizationId, Long schemeId, Long stateMachineId, List<StateMachineSchemeConfigDTO> schemeDTOs);

    /**
     * 创建默认配置
     *
     * @param organizationId
     * @param schemeId
     * @param stateMachineId
     */
    void createDefaultConfig(Long organizationId, Long schemeId, Long stateMachineId);

    /**
     * 更新默认配置
     *
     * @param organizationId
     * @param schemeId
     * @param stateMachineId
     */
    void updateDefaultConfig(Long organizationId, Long schemeId, Long stateMachineId);

    /**
     * 通过状态机方案id和问题类型id查询出状态机
     * @param stateMachineSchemeId
     * @param issueTypeId
     * @return
     */
    Long queryBySchemeIdAndIssueTypeId(Long organizationId, Long stateMachineSchemeId, Long issueTypeId);

}
