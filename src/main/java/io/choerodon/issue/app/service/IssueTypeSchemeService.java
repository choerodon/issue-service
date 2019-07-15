package io.choerodon.issue.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.issue.api.vo.IssueTypeSchemeSearchVO;
import io.choerodon.issue.api.vo.IssueTypeSchemeVO;
import io.choerodon.issue.api.vo.IssueTypeSchemeWithInfoVO;
import io.choerodon.issue.api.vo.IssueTypeVO;
import io.choerodon.issue.infra.dto.IssueTypeSchemeDTO;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/10
 */
public interface IssueTypeSchemeService {

    IssueTypeSchemeDTO baseCreate(IssueTypeSchemeDTO scheme);

    /**
     * 查询方案
     *
     * @param organizationId
     * @param issueTypeSchemeId
     * @return
     */
    IssueTypeSchemeVO queryById(Long organizationId, Long issueTypeSchemeId);

    /**
     * 创建方案
     *
     * @param organizationId
     * @param issueTypeSchemeVO
     * @return
     */
    IssueTypeSchemeVO create(Long organizationId, IssueTypeSchemeVO issueTypeSchemeVO);

    /**
     * 更新方案
     *
     * @param organizationId
     * @param issueTypeSchemeVO
     * @return
     */
    IssueTypeSchemeVO update(Long organizationId, IssueTypeSchemeVO issueTypeSchemeVO);

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
     * @param issueTypeVOS
     */
    void createConfig(Long organizationId, Long issueTypeSchemeId, List<IssueTypeVO> issueTypeVOS);

    /**
     * 查询该项目下的问题类型方案
     *
     * @param projectId
     * @return
     */
    IssueTypeSchemeVO queryByProjectId(Long projectId);

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

    PageInfo<IssueTypeSchemeWithInfoVO> queryIssueTypeSchemeList(PageRequest pageRequest, Long organizationId, IssueTypeSchemeSearchVO issueTypeSchemeVO);
}
