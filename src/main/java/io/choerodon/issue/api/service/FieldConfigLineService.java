package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.FieldConfigLineDTO;
import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.FieldConfigLine;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2018/8/23
 */
public interface FieldConfigLineService extends BaseService<FieldConfigLine> {
    FieldConfigLineDTO update(FieldConfigLineDTO fieldConfigLineDTO);

    List<FieldConfigLineDTO> listQuery(FieldConfigLineDTO fieldConfigLineDTO, String s);

    void createConfigLine(Long organizationId, Field field);
}
