package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.AgileIssueHeadDTO;
import io.choerodon.issue.api.dto.ObjectSchemeFieldCreateDTO;
import io.choerodon.issue.api.dto.ObjectSchemeFieldDetailDTO;
import io.choerodon.issue.api.dto.ObjectSchemeFieldUpdateDTO;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
public interface ObjectSchemeFieldService {
    /**
     * 组织层/项目层 获取字段列表
     *
     * @param organizationId
     * @param schemeCode
     * @return
     */
    Map<String, Object> listQuery(Long organizationId, Long projectId, String schemeCode);

    /**
     * 组织层/项目层 创建字段
     *
     * @param organizationId
     * @param projectId
     * @param fieldCreateDTO
     * @return
     */
    ObjectSchemeFieldDetailDTO create(Long organizationId, Long projectId, ObjectSchemeFieldCreateDTO fieldCreateDTO);

    /**
     * 组织层/项目层 查询字段详情
     *
     * @param organizationId
     * @param projectId
     * @param fieldId
     * @return
     */
    ObjectSchemeFieldDetailDTO queryById(Long organizationId, Long projectId, Long fieldId);

    /**
     * 组织层/项目层 删除字段
     *
     * @param organizationId
     * @param projectId
     * @param fieldId
     */
    void delete(Long organizationId, Long projectId, Long fieldId);

    /**
     * 组织层/项目层 更新字段
     *
     * @param organizationId
     * @param projectId
     * @param fieldId
     * @param updateDTO
     * @return
     */
    ObjectSchemeFieldDetailDTO update(Long organizationId, Long projectId, Long fieldId, ObjectSchemeFieldUpdateDTO updateDTO);

    /**
     * 组织层/项目层 字段名称是否重复
     *
     * @param organizationId
     * @param projectId
     * @param name
     * @param schemeCode
     * @return
     */
    Boolean checkName(Long organizationId, Long projectId, String name, String schemeCode);

    /**
     * 组织层/项目层 字段编码是否重复
     *
     * @param organizationId
     * @param projectId
     * @param code
     * @param schemeCode
     * @return
     */
    Boolean checkCode(Long organizationId, Long projectId, String code, String schemeCode);

    List<AgileIssueHeadDTO> getIssueHeadForAgile(Long organizationId, Long projectId, String schemeCode);
}
