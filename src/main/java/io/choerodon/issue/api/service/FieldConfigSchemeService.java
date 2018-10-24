package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.FieldConfigSchemeDTO;
import io.choerodon.issue.api.dto.FieldConfigSchemeDetailDTO;
import io.choerodon.issue.api.dto.FieldConfigSchemeLineDTO;
import io.choerodon.issue.domain.FieldConfigScheme;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @date 2018/8/27
 */
public interface FieldConfigSchemeService extends BaseService<FieldConfigScheme> {
    /**
     * 分页查询自定义字段
     *
     * @param pageRequest          分页对象
     * @param fieldConfigSchemeDTO 查询参数
     * @param params               模糊查询参数
     * @return 字段配置方案列表
     */
    Page<FieldConfigSchemeDTO> pageQuery(PageRequest pageRequest, FieldConfigSchemeDTO fieldConfigSchemeDTO, String params);

    /**
     * 创建字段配置方案
     *
     * @param organizationId             组织id
     * @param fieldConfigSchemeDetailDTO 字段配置方案参数
     * @return 字段配置方案
     */
    FieldConfigSchemeDetailDTO create(Long organizationId, FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO);

    Boolean checkName(Long organizationId, String name, Long id);

    void createConfig(Long organizationId, Long fieldConfigSchemeId, List<FieldConfigSchemeLineDTO> fieldConfigSchemeLineDTOList);

    FieldConfigSchemeDetailDTO querySchemeWithConfigById(Long organizationId, Long schemeId);

    FieldConfigSchemeDetailDTO update(Long organizationId, FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO);

    Boolean delete(Long organizationId, Long schemeId);

    Map<String, Object> checkDelete(Long organizationId, Long schemeId);
}
