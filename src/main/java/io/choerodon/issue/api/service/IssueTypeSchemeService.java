package io.choerodon.issue.api.service;

import io.choerodon.base.domain.PageRequest;
import com.github.pagehelper.PageInfo;
import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.dto.IssueTypeSchemeDTO;
import io.choerodon.issue.api.dto.IssueTypeSchemeSearchDTO;
import io.choerodon.issue.api.dto.IssueTypeSchemeWithInfoDTO;
import io.choerodon.issue.domain.IssueTypeScheme;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/10
 */
public interface IssueTypeSchemeService {

    /**
     * 查询方案
     *
     * @param organizationId
     * @param issueTypeSchemeId
     * @return
     */
    IssueTypeSchemeDTO queryById(Long organizationId, Long issueTypeSchemeId);

    /**
     * 创建方案
     *
     * @param organizationId
     * @param issueTypeSchemeDTO
     * @return
     */
    IssueTypeSchemeDTO create(Long organizationId, IssueTypeSchemeDTO issueTypeSchemeDTO);

    /**
     * 更新方案
     *
     * @param organizationId
     * @param issueTypeSchemeDTO
     * @return
     */
    IssueTypeSchemeDTO update(Long organizationId, IssueTypeSchemeDTO issueTypeSchemeDTO);

    /**
     * 校验是否可以删除
     *
     * @param organizationId
     * @param issueTypeSchemeId
     * @return
     */
    Map<String, Object> checkDelete(Long organizationId, Long issueTypeSchemeId);

    /**
     * 删除
     *
     * @param organizationId
     * @param issueTypeSchemeId
     * @return
     */
    Boolean delete(Long organizationId, Long issueTypeSchemeId);

    /**
     * 校验方案名是否可用
     *
     * @param organizationId
     * @param name
     * @return
     */
    Boolean checkName(Long organizationId, String name, Long id);

    /**
     * 创建方案配置
     *
     * @param organizationId
     * @param issueTypeSchemeId
     * @param issueTypeDTOS
     */
    void createConfig(Long organizationId, Long issueTypeSchemeId, List<IssueTypeDTO> issueTypeDTOS);

    /**
     * 查询该项目下的问题类型方案
     *
     * @param projectId
     * @return
     */
    IssueTypeSchemeDTO queryByProjectId(Long projectId);

    /**
     * 创建项目初始化问题类型方案
     *
     * @param projectId
     * @param projectCode
     * @return
     */
    void initByConsumeCreateProject(Long projectId, String projectCode);

    /**
     * 创建项目群初始化问题类型方案
     *
     * @param projectId
     * @param projectCode
     * @return
     */
    void initByConsumeCreateProgram(Long projectId, String projectCode);

    PageInfo<IssueTypeSchemeWithInfoDTO> queryIssueTypeSchemeList(PageRequest pageRequest, Long organizationId, IssueTypeSchemeSearchDTO issueTypeSchemeDTO);
}
