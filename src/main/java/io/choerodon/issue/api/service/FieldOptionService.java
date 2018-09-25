package io.choerodon.issue.api.service;


import io.choerodon.issue.api.dto.FieldOptionDTO;
import io.choerodon.issue.domain.FieldOption;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

public interface FieldOptionService extends BaseService<FieldOption> {

    Boolean delete(Long fieldId, Long fieldOptionId);

    Boolean checkValue(Long fieldId, String value, Long id, Long parentId);

    Map<String, Object> checkDelete(Long fieldId, Long fieldOptionId);

    List<FieldOptionDTO> queryByFieldId(Long organizationId, Long fieldId);

    /**
     * 更新字段选项
     *
     * @param organizationId
     * @param fieldId
     * @param fieldOptionDTOS
     * @return
     */
    void updateFieldOptionLoop(Long organizationId, Long fieldId, Long parentId, List<FieldOptionDTO> fieldOptionDTOS);
}
