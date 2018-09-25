package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.FieldConfigSchemeLineDTO;
import io.choerodon.issue.api.service.FieldConfigSchemeLineService;
import io.choerodon.issue.domain.FieldConfigSchemeLine;
import io.choerodon.issue.infra.mapper.FieldConfigSchemeLineMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @date 2018/8/27
 */

@Component
@RefreshScope
public class FieldConfigSchemeLineServiceImpl extends BaseServiceImpl<FieldConfigSchemeLine> implements FieldConfigSchemeLineService {
    @Autowired
    private FieldConfigSchemeLineMapper fieldConfigSchemeLineMapper;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<FieldConfigSchemeLineDTO> queryBySchemeId(Long organizationId, Long schemeId) {
        List<FieldConfigSchemeLine> fieldConfigSchemeLine = fieldConfigSchemeLineMapper.queryBySchemeId(schemeId);
        return modelMapper.map(fieldConfigSchemeLine, new TypeToken<List<FieldConfigSchemeLineDTO>>() {
        }.getType());
    }

    @Override
    public FieldConfigSchemeLineDTO create(Long organizationId, FieldConfigSchemeLineDTO fieldConfigSchemeLineDTO) {
        if (fieldConfigSchemeLineDTO.getSchemeId() == null || fieldConfigSchemeLineDTO.getIssueTypeId() == null || fieldConfigSchemeLineDTO.getFieldConfigId() == null) {
            throw new CommonException("error.fieldConfigSchemeLine.create");
        }
        if (checkId(fieldConfigSchemeLineDTO.getIssueTypeId(), fieldConfigSchemeLineDTO.getSchemeId())) {
            throw new CommonException("error.fieldConfigSchemeLine.checkId");
        }
        FieldConfigSchemeLine fieldConfigSchemeLine = modelMapper.map(fieldConfigSchemeLineDTO, FieldConfigSchemeLine.class);
        if (fieldConfigSchemeLineMapper.insert(fieldConfigSchemeLine) != 1) {
            throw new CommonException("error.fieldConfigSchemeLine.create");
        }
        fieldConfigSchemeLine = fieldConfigSchemeLineMapper.selectByPrimaryKey(fieldConfigSchemeLine.getId());
        return modelMapper.map(fieldConfigSchemeLine, FieldConfigSchemeLineDTO.class);
    }

    @Override
    public FieldConfigSchemeLineDTO update(Long organizationId, Long schemeId, Long issueTypeId, Long id, FieldConfigSchemeLineDTO fieldConfigSchemeLineDTO) {
        FieldConfigSchemeLine fieldConfigSchemeLine = modelMapper.map(fieldConfigSchemeLineDTO, FieldConfigSchemeLine.class);
        if (fieldConfigSchemeLine.getFieldConfigId() == null) {
            throw new CommonException("error.fieldConfigSchemeLine.update");
        }
        int isUpdate = fieldConfigSchemeLineMapper.updateByPrimaryKey(fieldConfigSchemeLine);
        if (isUpdate != 1) {
            throw new CommonException("error.fieldConfigSchemeLine.update");
        }
        fieldConfigSchemeLine = fieldConfigSchemeLineMapper.selectByPrimaryKey(fieldConfigSchemeLine.getId());
        return modelMapper.map(fieldConfigSchemeLine, FieldConfigSchemeLineDTO.class);
    }

    @Override
    public Boolean delete(Long organizationId, Long id) {
        Map<String, Object> result = checkDelete(organizationId, id);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = fieldConfigSchemeLineMapper.deleteByPrimaryKey(id);
            if (isDelete != 1) {
                throw new CommonException("error.state.delete");
            }
        } else {
            return false;
        }
        //校验
        return true;
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        FieldConfigSchemeLine fieldConfigSchemeLine = fieldConfigSchemeLineMapper.selectByPrimaryKey(id);
        if (fieldConfigSchemeLine == null) {
            throw new CommonException("error.base.notFound");
        }
        return result;
    }


    private boolean checkId(Long issueTypeId, Long schemeId) {
        FieldConfigSchemeLine select = new FieldConfigSchemeLine();
        select.setIssueTypeId(issueTypeId);
        select.setSchemeId(schemeId);
        select = fieldConfigSchemeLineMapper.selectOne(select);
        return (select != null);
    }

    @Override
    public Long getFieldConfigIdByIssueTypeId(Long schemeId, Long issueTypeId) {
        FieldConfigSchemeLine line = new FieldConfigSchemeLine();
        line.setSchemeId(schemeId);
        line.setIssueTypeId(issueTypeId);
        List<FieldConfigSchemeLine> lines = fieldConfigSchemeLineMapper.select(line);
        if (lines.isEmpty()) {
            line.setIssueTypeId(0L);
            lines = fieldConfigSchemeLineMapper.select(line);
            if (lines.isEmpty()) {
                return null;
            }
        }
        return lines.get(0).getFieldConfigId();
    }
}
