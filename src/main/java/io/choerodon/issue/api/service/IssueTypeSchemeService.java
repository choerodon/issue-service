package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.dto.IssueTypeSchemeDTO;
import io.choerodon.issue.domain.IssueTypeScheme;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/10
 */
public interface IssueTypeSchemeService extends BaseService<IssueTypeScheme> {

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
     * 分页
     *
     * @param pageRequest
     * @param issueTypeSchemeDTO
     * @param param
     * @return
     */
    Page<IssueTypeSchemeDTO> pageQuery(PageRequest pageRequest, IssueTypeSchemeDTO issueTypeSchemeDTO, String param);

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
     * @param projectId
     * @return
     */
    IssueTypeSchemeDTO queryByProjectId(Long projectId);
}
