package io.choerodon.issue.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.api.dto.payload.ProjectEvent;
import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;

/**
 * @author shinan.chen
 * @Date 2018/8/2
 */
public interface StateMachineSchemeService extends BaseService<StateMachineScheme> {

    /**
     * 分页查询状态机方案
     *
     * @param pageRequest 分页对象
     * @param schemeDTO   查询参数
     * @param params      模糊查询参数
     * @return 状态机方案列表
     */
    Page<StateMachineSchemeDTO> pageQuery(Long organizationId, PageRequest pageRequest, StateMachineSchemeDTO schemeDTO, String params);

    /**
     * 创建状态机方案
     *
     * @param organizationId 组织id
     * @param schemeDTO      状态机方案对象
     * @return 状态机方案对象
     */
    StateMachineSchemeDTO create(Long organizationId, StateMachineSchemeDTO schemeDTO);

    /**
     * 更新状态机方案
     *
     * @param organizationId 组织id
     * @param schemeId       方案id
     * @param schemeDTO      方案对象
     * @return 方案对象
     */
    StateMachineSchemeDTO update(Long organizationId, Long schemeId, StateMachineSchemeDTO schemeDTO);

    /**
     * 删除状态机方案
     *
     * @param organizationId 组织id
     * @param schemeId       方案id
     * @return
     */
    Boolean delete(Long organizationId, Long schemeId);

    /**
     * 获取状态机方案及其配置
     *
     * @param organizationId 组织id
     * @param schemeId       方案id
     * @return 状态机方案及配置
     */
    StateMachineSchemeDTO querySchemeWithConfigById(Boolean isDraft, Long organizationId, Long schemeId);

    /**
     * 校验名字是否未被使用
     *
     * @param organizationId 组织id
     * @param name           名称
     * @return
     */
    Boolean checkName(Long organizationId, String name);

    /**
     * 根据状态机id查询所使用到该状态机的方案，包含发布使用与草稿使用
     *
     * @param organizationId 组织id
     * @param stateMachineId 状态机id
     * @return 方案列表
     */
    List<StateMachineSchemeDTO> querySchemeByStateMachineId(Long organizationId, Long stateMachineId);

    /**
     * 创建项目时，初始化方案
     *
     * @param projectEvent
     */
    void initByConsumeCreateProject(ProjectEvent projectEvent);

    /**
     * 若项目关联状态机方案，设置状态机方案、状态机为活跃
     *
     * @param schemeId
     */
    void activeSchemeWithRefProjectConfig(Long schemeId);

    /**
     * 更新发布进度
     *
     * @param organizationId organizationId
     * @param schemeId       schemeId
     * @param deployProgress deployProgress
     * @return Boolean
     */
    Boolean updateDeployProgress(Long organizationId, Long schemeId, Integer deployProgress);
}
