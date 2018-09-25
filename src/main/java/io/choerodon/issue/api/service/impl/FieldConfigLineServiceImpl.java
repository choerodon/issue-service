package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.FieldConfigLineDTO;
import io.choerodon.issue.api.service.FieldConfigLineService;
import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.FieldConfig;
import io.choerodon.issue.domain.FieldConfigLine;
import io.choerodon.issue.infra.mapper.FieldConfigLineMapper;
import io.choerodon.issue.infra.mapper.FieldConfigMapper;
import io.choerodon.issue.infra.mapper.FieldMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2018/8/23
 */

@Component
@RefreshScope
public class FieldConfigLineServiceImpl extends BaseServiceImpl<FieldConfigLine> implements FieldConfigLineService {
    private static final String YES = "1";
    private static final String NO = "0";
    @Autowired
    FieldConfigMapper fieldConfigMapper;
    @Autowired
    FieldMapper fieldMapper;
    @Autowired
    FieldConfigLineMapper fieldConfigLineMapper;

    private final ModelMapper modelMapper = new ModelMapper();


    @Override
    public FieldConfigLineDTO update(FieldConfigLineDTO fieldConfigLineDTO) {
        FieldConfigLine fieldConfigLine = modelMapper.map(fieldConfigLineDTO, FieldConfigLine.class);
        int isUpdate = fieldConfigLineMapper.updateByPrimaryKey(fieldConfigLine);
        if (isUpdate != 1) {
            throw new CommonException("error.fieldConfig.update");
        }
        fieldConfigLine = fieldConfigLineMapper.queryById(fieldConfigLine.getId());
        return modelMapper.map(fieldConfigLine, FieldConfigLineDTO.class);
    }


    @Override
    public List<FieldConfigLineDTO> listQuery(FieldConfigLineDTO fieldConfigLineDTO, String params) {
        FieldConfigLine fieldConfigLine=modelMapper.map(fieldConfigLineDTO, FieldConfigLine.class);
        List<FieldConfigLine> fieldConfigLineList=fieldConfigLineMapper.searchFull(fieldConfigLine.getFieldConfigId(),params);

        return modelMapper.map(fieldConfigLineList, new TypeToken<List<FieldConfigLineDTO>>() {
        }.getType());
    }

    @Override
    public void createConfigLine(Long organizationId, Field field) {
        FieldConfig select = new FieldConfig();
        select.setOrganizationId(organizationId);
        List<FieldConfig> fieldConfigList = fieldConfigMapper.select(select);
        if (!fieldConfigList.isEmpty()) {
            for (FieldConfig record : fieldConfigList) {
                FieldConfigLine fieldConfigLine = new FieldConfigLine();
                fieldConfigLine.setFieldConfigId(record.getId());
                fieldConfigLine.setFieldId(field.getId());
                fieldConfigLine.setIsDisplay(NO);
                fieldConfigLine.setIsRequired(NO);
                if (fieldConfigLineMapper.insert(fieldConfigLine) != 1) {
                    throw new CommonException("error.fieldConfigLine.create");
                }
            }

        }

    }
}
