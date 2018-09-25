package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.FieldConfigDTO;
import io.choerodon.issue.api.dto.FieldConfigDetailDTO;
import io.choerodon.issue.domain.FieldConfig;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @date 2018/8/23
 */
public interface FieldConfigService extends BaseService<FieldConfig> {


    /**
     * 分页查询字段配置
     *
     * @param pageRequest    分页对象
     * @param fieldConfigDTO 查询参数
     * @param params         模糊查询参数
     * @return 字段配置列表
     */
    Page<FieldConfigDTO> pageQuery(Long organizationId, PageRequest pageRequest, FieldConfigDTO fieldConfigDTO, String params);

    /**
     * 创建字段配置
     *
     * @param organizationId 组织id
     * @param fieldConfigDTO 字段配置对象
     * @return 字段配置
     */
    FieldConfigDTO create(Long organizationId, FieldConfigDTO fieldConfigDTO);

    /**
     * 校验字段配置名是否可用
     *
     * @param organizationId
     * @param name
     * @return
     */
    Boolean checkName(Long organizationId, String name, Long id);

    /**
     * 删除字段配置
     *
     * @param organizationId 组织id
     * @param fieldId        字段ID
     * @return
     */
    Boolean delete(Long organizationId, Long fieldId);

    /**
     * 校验是否可以删除
     *
     * @param organizationId
     * @param id
     * @return
     */
    Map<String, Object> checkDelete(Long organizationId, Long id);

    /**
     * 查询所有数据
     *
     * @param organizationId
     * @return 字段配置列表
     */
    List<FieldConfigDTO> queryByOrgId(Long organizationId);

    FieldConfigDetailDTO queryByFieldConfigId(Long organizationId, Long fieldConfigId);

    FieldConfigDetailDTO update(FieldConfigDetailDTO fieldConfigDetailDTO);
}
