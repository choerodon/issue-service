package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.FieldConfigSchemeLineDTO;
import io.choerodon.issue.domain.FieldConfigSchemeLine;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @date 2018/8/27
 */
public interface FieldConfigSchemeLineService extends BaseService<FieldConfigSchemeLine> {
    List<FieldConfigSchemeLineDTO> queryBySchemeId(Long organizationId, Long schemeId);

    /**
     * 添加关联
     *
     * @param organizationId           组织id
     * @param fieldConfigSchemeLineDTO 关联对象
     * @return 关联
     */
    FieldConfigSchemeLineDTO create(Long organizationId, FieldConfigSchemeLineDTO fieldConfigSchemeLineDTO);

    FieldConfigSchemeLineDTO update(Long organizationId, Long schemeId, Long issueTypeId, Long id, FieldConfigSchemeLineDTO fieldConfigSchemeLineDTO);

    Boolean delete(Long organizationId, Long id);

    Map<String, Object> checkDelete(Long organizationId, Long id);

    /**
     * 通过问题类型Id获取到字段配置id（默认值的处理）
     * @param schemeId
     * @param issueTypeId
     * @return
     */
    Long getFieldConfigIdByIssueTypeId(Long schemeId, Long issueTypeId);
}
