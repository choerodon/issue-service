package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.PriorityDTO;

import java.util.List;
import java.util.Map;

/**
 * @author cong.cheng
 * @Date 2018/8/21
 */
public interface PriorityService {
    /**
     * 查询组织下优先级列表
     *
     * @param priorityDTO 分页展示优先级对象
     * @param param       模糊分页
     * @return 优先级列表
     */
    List<PriorityDTO> selectAll(PriorityDTO priorityDTO, String param);

    /**
     * 在组织下创建新的优先级
     *
     * @param organizationId 组织id
     * @param priorityDTO    创建优先级对象
     * @return 新的优先级对象
     */
    PriorityDTO create(Long organizationId, PriorityDTO priorityDTO);

    /**
     * 在组织下删除优先级
     *
     * @param organizationId 组织id
     * @param priorityId     优先级id
     * @return 删除是否成功 true or false
     */
    Boolean delete(Long organizationId, Long priorityId, Long changePriorityId);

    /**
     * 更新优先级
     *
     * @param priorityDTO 更新优先级对象
     * @return 更新的优先级对象
     */
    PriorityDTO update(PriorityDTO priorityDTO);

    /**
     * 检查组织下的优先级名称是否重复
     *
     * @param organizationId 组织id
     * @param name           创建或更新的优先级名称
     * @return 是否重复 true or false
     */
    Boolean checkName(Long organizationId, String name);

    /**
     * 根据id更新优先级的顺序
     *
     * @param list           优先级对象列表
     * @param organizationId 组织
     * @return 更新是否成功
     */
    List<PriorityDTO> updateByList(List<PriorityDTO> list, Long organizationId);

    Map<Long, PriorityDTO> queryByOrganizationId(Long organizationId);

    PriorityDTO queryDefaultByOrganizationId(Long organizationId);

    List<PriorityDTO> queryByOrganizationIdList(Long organizationId);

    PriorityDTO queryById(Long organizationId, Long id);

    Map<Long, Map<String, Long>> initProrityByOrganization(List<Long> organizationIds);

    /**
     * 生效/失效优先级
     *
     * @param organizationId
     * @param id
     * @param enable
     * @return
     */
    PriorityDTO enablePriority(Long organizationId, Long id, Boolean enable);

    /**
     * 校验删除优先级
     *
     * @param organizationId
     * @param id
     * @return
     */
    Long checkDelete(Long organizationId, Long id);
}
