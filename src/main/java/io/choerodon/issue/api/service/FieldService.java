package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.FieldDTO;
import io.choerodon.issue.api.dto.FieldDetailDTO;
import io.choerodon.issue.domain.Field;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @Date 2018/8/2
 */
public interface FieldService extends BaseService<Field> {

    /**
     * 分页查询自定义字段
     *
     * @param pageRequest 分页对象
     * @param fieldDTO    查询参数
     * @param params      模糊查询参数
     * @return 自定义字段列表
     */
    Page<FieldDTO> pageQuery(Long organizationId, PageRequest pageRequest, FieldDTO fieldDTO, String params);

    /**
     * 创建自定义字段
     *
     * @param organizationId 组织id
     * @param fieldDTO       自定义字段对象
     * @return 自定义字段
     */
    FieldDTO create(Long organizationId, FieldDTO fieldDTO);

    /**
     * 更新自定义字段
     *
     * @param fieldDetailDTO 字段对象
     * @return 字段对象
     */
    FieldDetailDTO update(FieldDetailDTO fieldDetailDTO);

    /**
     * 删除字段
     *
     * @param organizationId 组织id
     * @param fieldId        自定义字段id
     * @return
     */
    Boolean delete(Long organizationId, Long fieldId);

    Boolean checkName(Long organizationId, String name, Long id);

    Map<String, Object> checkDelete(Long organizationId, Long fieldId);

    FieldDetailDTO queryById(Long organizationId, Long id);

    List<FieldDTO> listQuery(Long organizationId, FieldDTO fieldDTO, String params);

    /**
     * 获取字段关联页面
     *
     * @param organizationId
     * @param fieldId
     * @return
     */
    List<Long> queryRelatedPage(Long organizationId, Long fieldId);

    /**
     * 更新字段关联页面
     *
     * @param organizationId
     * @param fieldId
     * @param pageIds
     * @return
     */
    List<Long> updateRelatedPage(Long organizationId, Long fieldId, List<Long> pageIds);

    /**
     * 获取字段的默认值（若有option的，取option中的值）
     * @param fieldId
     * @return
     */
    String getDefaultValue(Long organizationId, Long fieldId);
}
