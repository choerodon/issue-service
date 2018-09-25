package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.PageSchemeDTO;
import io.choerodon.issue.api.dto.PageSchemeDetailDTO;
import io.choerodon.issue.api.dto.PageSchemeLineDTO;
import io.choerodon.issue.domain.PageScheme;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/23
 */
public interface PageSchemeService extends BaseService<PageScheme> {

    /**
     * 查询方案
     *
     * @param organizationId
     * @param pageSchemeId
     * @return
     */
    PageSchemeDetailDTO queryById(Long organizationId, Long pageSchemeId);

    /**
     * 创建方案
     *
     * @param organizationId
     * @param pageSchemeDetailDTO
     * @return
     */
    PageSchemeDetailDTO create(Long organizationId, PageSchemeDetailDTO pageSchemeDetailDTO);

    /**
     * 更新方案
     *
     * @param organizationId
     * @param pageSchemeDetailDTO
     * @return
     */
    PageSchemeDetailDTO update(Long organizationId, PageSchemeDetailDTO pageSchemeDetailDTO);

    /**
     * 校验是否可以删除
     *
     * @param organizationId
     * @param pageSchemeId
     * @return
     */
    Map<String, Object> checkDelete(Long organizationId, Long pageSchemeId);

    /**
     * 删除
     *
     * @param organizationId
     * @param pageSchemeId
     * @return
     */
    Boolean delete(Long organizationId, Long pageSchemeId);

    /**
     * 分页
     *
     * @param pageRequest
     * @param pageSchemeDTO
     * @param param
     * @return
     */
    Page<PageSchemeDTO> pageQuery(Long organizationId, PageRequest pageRequest, PageSchemeDTO pageSchemeDTO, String param);

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
     * @param pageSchemeId
     * @param pageSchemeLineDTOS
     */
    void createConfig(Long organizationId, Long pageSchemeId, List<PageSchemeLineDTO> pageSchemeLineDTOS);

    /**
     * 查询组织下所有方案
     * @param organizationId 组织id
     * @return 方案列表
     */
    List<PageSchemeDetailDTO> queryAll(Long organizationId);
}
