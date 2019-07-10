package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.FieldOptionDTO;
import io.choerodon.issue.api.dto.FieldOptionUpdateDTO;
import io.choerodon.issue.api.dto.PageFieldViewDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface FieldOptionService {
    /**
     * 处理字段选项
     *
     * @param organizationId
     * @param fieldId
     * @param newOptions
     */
    String handleFieldOption(Long organizationId, Long fieldId, List<FieldOptionUpdateDTO> newOptions);

    /**
     * 组织层/项目层 根据字段id获取字段选项列表
     *
     * @param organizationId
     * @param fieldId
     * @return
     */
    List<FieldOptionDTO> queryByFieldId(Long organizationId, Long fieldId);

    /**
     * 组织层/项目层 创建字段选项
     *
     * @param organizationId
     * @param fieldId
     * @param updateDTO
     * @return
     */
    void create(Long organizationId, Long fieldId, FieldOptionUpdateDTO updateDTO);

    /**
     * 组织层/项目层 根据字段id删除所有字段选项
     *
     * @param organizationId
     * @param fieldId
     */
    void deleteByFieldId(Long organizationId, Long fieldId);

    /**
     * 填充字段选项
     *
     * @param organizationId
     * @param projectId
     * @param pageFieldViews
     */
    void fillOptions(Long organizationId, Long projectId, List<PageFieldViewDTO> pageFieldViews);
}
