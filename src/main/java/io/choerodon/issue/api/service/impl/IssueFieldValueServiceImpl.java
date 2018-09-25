package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.*;
import io.choerodon.issue.api.service.FieldOptionService;
import io.choerodon.issue.api.service.FieldService;
import io.choerodon.issue.api.service.IssueFieldValueService;
import io.choerodon.issue.api.service.ProjectConfigService;
import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.FieldConfigLine;
import io.choerodon.issue.domain.IssueFieldValue;
import io.choerodon.issue.infra.enums.FieldType;
import io.choerodon.issue.infra.mapper.FieldMapper;
import io.choerodon.issue.infra.mapper.IssueFieldValueMapper;
import io.choerodon.issue.infra.mapper.IssueMapper;
import io.choerodon.issue.infra.utils.ProjectUtil;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @Date 2018/9/4
 */
@Component
@RefreshScope
public class IssueFieldValueServiceImpl extends BaseServiceImpl<IssueFieldValue> implements IssueFieldValueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueFieldValueServiceImpl.class);

    private static final String NO = "0";
    private static final String YES = "1";
    @Autowired
    private IssueFieldValueMapper issueFieldValueMapper;
    @Autowired
    private ProjectConfigService projectConfigService;
    @Autowired
    private FieldOptionService fieldOptionService;
    @Autowired
    private FieldService fieldService;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private FieldMapper fieldMapper;
    @Autowired
    private ProjectUtil projectUtil;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<IssueFieldValue> queryByIssueId(Long issueId) {
        List<IssueFieldValue> values = issueFieldValueMapper.queryByIssueId(issueId);
        List<IssueFieldValue> newValues = new ArrayList<>();
        //多选值转化成一个值用于前端显示
        values.stream().collect(Collectors.groupingBy(IssueFieldValue::getFieldId)).entrySet().stream().forEach(x -> {
            List<IssueFieldValue> ifvs = x.getValue();
            if (ifvs.size() == 1) {
                newValues.add(ifvs.get(0));
            } else {
                ifvs.get(0).setFieldValue(ifvs.stream().map(IssueFieldValue::getFieldValue).collect(Collectors.joining(",")));
                newValues.add(ifvs.get(0));
            }
        });
        return newValues;
    }

    @Override
    public List<IssueFieldValueDTO> queryByIssueTypeIdAndPageType(Long projectId, Long issueTypeId, String pageType) {
        Long organizationId = projectUtil.getOrganizationId(projectId);
        List<IssueFieldValueDTO> result = new ArrayList<>();
        List<Field> fields = projectConfigService.queryFieldByIssueTypeAndPageType(organizationId, projectId, issueTypeId, pageType);
        List<FieldConfigLine> fieldConfigLines = projectConfigService.queryFieldConfigLinesByIssueType(organizationId, projectId, issueTypeId);
        Map<Long, FieldConfigLine> lineIdMap = fieldConfigLines.stream().collect(Collectors.toMap(FieldConfigLine::getFieldId, x -> x));
        for (Field field : fields) {
            FieldConfigLine line = lineIdMap.get(field.getId());
            if (line == null) {
                continue;
            }
            IssueFieldValueDTO dto = new IssueFieldValueDTO();
            dto.setFieldId(field.getId());
            dto.setFieldName(field.getName());
            dto.setFieldValue(field.getDefaultValue());
            dto.setFieldType(field.getType());
            dto.setFieldExtraConfig(field.getExtraConfig());
            dto.setIsDisplay(line.getIsDisplay());
            dto.setIsRequired(line.getIsRequired());
            //若有字段选项则一同返回
            if (FieldType.hasOption(field.getType())) {
                List<FieldOptionDTO> fieldOptionDTOS = fieldOptionService.queryByFieldId(organizationId, field.getId());
                if (fieldOptionDTOS != null) {
                    dto.setFieldOptions(fieldOptionDTOS);
                    dto.setFieldValue(fieldOptionDTOS.stream().filter(x -> x.getIsDefault().equals(YES)).map(x -> x.getId().toString()).collect(Collectors.joining(",")));
                }
            }
            fieldConfigLines.remove(line);
            result.add(dto);
        }
        return result;
    }

    @Override
    public void supplyFieldValue(Long issueId, List<IssueFieldValueDTO> fieldValues) {
        List<IssueFieldValue> values = queryByIssueId(issueId);
        Map<Long, IssueFieldValue> map = values.stream().collect(Collectors.toMap(IssueFieldValue::getFieldId, x -> x));
        fieldValues.stream().forEach(dto -> {
            IssueFieldValue value = map.get(dto.getFieldId());
            if (value != null) {
                dto.setId(value.getId());
                dto.setFieldValue(value.getFieldValue());
                dto.setObjectVersionNumber(value.getObjectVersionNumber());
            }
        });
    }

    @Override
    public void createFieldValues(Long projectId, Long issueId, Long issueTypeId, List<IssueFieldValueDTO> fieldValues) {
        //获取字段配置
        List<FieldConfigLine> fieldConfigLines = projectConfigService.queryFieldConfigLinesByIssueType(projectUtil.getOrganizationId(projectId), projectId, issueTypeId);
        Map<Long, FieldConfigLine> lineIdMap = fieldConfigLines.stream().collect(Collectors.toMap(FieldConfigLine::getFieldId, x -> x));

        if (fieldValues != null) {
            fieldValues.stream().forEach(value -> {
                value.setId(null);
                if (value.getFieldId() == null) {
                    throw new CommonException("error.issueFieldValue.fieldId.null");
                }

                FieldConfigLine line = lineIdMap.get(value.getFieldId());
                if (line != null) {
                    value.setIsRequired(line.getIsRequired());
                    createAndUpdate(projectId, issueId, value);
                }

            });
        }
    }

    @Override
    public Long updateFieldValue(Long projectId, Long issueId, IssueFieldValueDTO fieldValue) {
        //获取字段配置
        Long issueTypeId = issueMapper.selectByPrimaryKey(issueId).getIssueTypeId();
        List<FieldConfigLine> fieldConfigLines = projectConfigService.queryFieldConfigLinesByIssueType(projectUtil.getOrganizationId(projectId), projectId, issueTypeId);
        Map<Long, FieldConfigLine> lineIdMap = fieldConfigLines.stream().collect(Collectors.toMap(FieldConfigLine::getFieldId, x -> x));

        if (fieldValue.getFieldId() == null) {
            throw new CommonException("error.issueFieldValue.fieldId.null");
        }

        FieldConfigLine line = lineIdMap.get(fieldValue.getFieldId());
        if (line == null) {
            throw new CommonException("error.fieldConfigLine.null");
        }
        fieldValue.setIsRequired(line.getIsRequired());

        return createAndUpdate(projectId, issueId, fieldValue);
    }

    public Long createAndUpdate(Long projectId, Long issueId, IssueFieldValueDTO value) {
        value.setIssueId(issueId);
        //设置默认值
        if (value.getFieldValue() == null || value.getFieldValue().equals("")) {
            value.setFieldValue(fieldService.getDefaultValue(projectUtil.getOrganizationId(projectId), value.getFieldId()));
        }
        //必填字段没有填写，抛异常
        if (value.getIsRequired().equals(YES) && (value.getFieldValue() == null || value.getFieldValue().equals(""))) {
            throw new CommonException("error.issueFieldValue.illegal");
        }

        if (value.getId() != null && !value.getId().equals(0L)) {
            //是修改操作先进行删除
            IssueFieldValue ifv = new IssueFieldValue();
            ifv.setFieldId(value.getFieldId());
            ifv.setIssueId(issueId);
            if (issueFieldValueMapper.delete(ifv) == 0) {
                throw new CommonException("error.issueFieldValue.update");
            }
        }
        //新增值
        if (value.getFieldValue() != null && !value.getFieldValue().equals("")) {
            IssueFieldValue issueFieldValue = modelMapper.map(value, IssueFieldValue.class);
            issueFieldValue.setProjectId(projectId);
            for(String fvalue:value.getFieldValue().split(",")){
                issueFieldValue.setId(null);
                issueFieldValue.setFieldValue(fvalue);
                if (issueFieldValueMapper.insert(issueFieldValue) != 1) {
                    throw new CommonException("error.issueFieldValue.create");
                }
            }
            return issueFieldValue.getId();
        }
        return null;
    }

    @Override
    public void handleFieldValueSearch(Long projectId, SearchDTO searchDTO) {
        if (searchDTO != null && searchDTO.getCustomFieldSearchArgs() != null) {
            List<Field> fields = fieldMapper.queryByOrgId(projectUtil.getOrganizationId(projectId));
            Map<Long, Field> fieldsMap = fields.stream().collect(Collectors.toMap(Field::getId, x -> x));
            Map<Long, String> fieldArgs = searchDTO.getCustomFieldSearchArgs();
            List<IssueFieldValueSearchDTO> values = new ArrayList<>();
            for (Map.Entry<Long, String> entry : fieldArgs.entrySet()) {
                Field field = fieldsMap.get(entry.getKey());
                IssueFieldValueSearchDTO value = new IssueFieldValueSearchDTO();
                value.setFieldId(entry.getKey());
                value.setFieldType(field.getType());
                value.setFieldValue(entry.getValue());
                values.add(value);
            }
            List<Long> issueIds = issueFieldValueMapper.queryIssueIdsBySearch(projectId, values);
            searchDTO.getSearchArgsIds().put("issueIds", issueIds);
        }
    }
}
