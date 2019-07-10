package io.choerodon.issue.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.FieldOptionDTO;
import io.choerodon.issue.api.dto.FieldOptionUpdateDTO;
import io.choerodon.issue.api.dto.PageFieldViewDTO;
import io.choerodon.issue.api.service.FieldOptionService;
import io.choerodon.issue.api.service.FieldValueService;
import io.choerodon.issue.domain.FieldOption;
import io.choerodon.issue.infra.mapper.FieldOptionMapper;
import io.choerodon.issue.infra.repository.FieldOptionRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
@Component
public class FieldOptionServiceImpl implements FieldOptionService {
    @Autowired
    private FieldOptionMapper fieldOptionMapper;
    @Autowired
    private FieldOptionRepository fieldOptionRepository;
    @Autowired
    private FieldValueService fieldValueService;

    private ModelMapper modelMapper = new ModelMapper();
    private static final String ERROR_OPTION_ILLEGAL = "error.fieldOption.illegal";

    @Override
    public synchronized String handleFieldOption(Long organizationId, Long fieldId, List<FieldOptionUpdateDTO> newOptions) {
        List<FieldOptionDTO> oldOptions = queryByFieldId(organizationId, fieldId);
        if (newOptions == null || oldOptions == null) {
            throw new CommonException(ERROR_OPTION_ILLEGAL);
        }
        //重名校验
        if (newOptions.stream().map(FieldOptionUpdateDTO::getValue).collect(Collectors.toSet()).size() != newOptions.size()) {
            throw new CommonException(ERROR_OPTION_ILLEGAL);
        }
        if (newOptions.stream().map(FieldOptionUpdateDTO::getCode).collect(Collectors.toSet()).size() != newOptions.size()) {
            throw new CommonException(ERROR_OPTION_ILLEGAL);
        }
        //删除校验
        List<Long> oldIds = oldOptions.stream().map(FieldOptionDTO::getId).collect(Collectors.toList());
        List<Long> newIds = newOptions.stream().map(FieldOptionUpdateDTO::getId).collect(Collectors.toList());
        List<Long> deleteIds = new ArrayList<>(oldIds);
        deleteIds.removeAll(newIds);
        fieldValueService.deleteByOptionIds(fieldId, deleteIds);
        //先删除所有选项
        deleteByFieldId(organizationId, fieldId);
        //设置排序
        AtomicInteger seq = new AtomicInteger(0);
        newOptions.forEach(option -> option.setSequence(seq.getAndIncrement()));
        //处理增加
        newOptions.stream().filter(x -> "add".equals(x.getStatus())).forEach(addOption -> {
            addOption.setId(null);
            create(organizationId, fieldId, addOption);
        });
        //处理修改与未修改
        newOptions.stream().filter(x -> !"add".equals(x.getStatus())).forEach(updateOption -> {
            if (updateOption.getId() == null) {
                throw new CommonException(ERROR_OPTION_ILLEGAL);
            }
            create(organizationId, fieldId, updateOption);
        });
        //处理默认值
        return newOptions.stream().filter(x -> x.getIsDefault() != null && x.getIsDefault()).map(x -> x.getId().toString()).collect(Collectors.joining(","));
    }


    @Override
    public List<FieldOptionDTO> queryByFieldId(Long organizationId, Long fieldId) {
        return modelMapper.map(fieldOptionMapper.selectByFieldId(organizationId, fieldId), new TypeToken<List<FieldOptionDTO>>() {
        }.getType());
    }

    @Override
    public void create(Long organizationId, Long fieldId, FieldOptionUpdateDTO optionDTO) {
        FieldOption fieldOption = modelMapper.map(optionDTO, FieldOption.class);
        fieldOption.setOrganizationId(organizationId);
        fieldOption.setFieldId(fieldId);
        fieldOptionRepository.create(fieldOption);
        optionDTO.setId(fieldOption.getId());
    }

    @Override
    public void deleteByFieldId(Long organizationId, Long fieldId) {
        FieldOption delete = new FieldOption();
        delete.setFieldId(fieldId);
        delete.setOrganizationId(organizationId);
        fieldOptionMapper.delete(delete);
    }

    @Override
    public void fillOptions(Long organizationId, Long projectId, List<PageFieldViewDTO> pageFieldViews) {
        List<Long> fieldIds = pageFieldViews.stream().map(PageFieldViewDTO::getFieldId).collect(Collectors.toList());
        if (fieldIds != null && !fieldIds.isEmpty()) {
            List<FieldOptionDTO> options = modelMapper.map(fieldOptionMapper.selectByFieldIds(organizationId, fieldIds), new TypeToken<List<FieldOptionDTO>>() {
            }.getType());
            Map<Long, List<FieldOptionDTO>> optionGroup = options.stream().collect(Collectors.groupingBy(FieldOptionDTO::getFieldId));
            pageFieldViews.forEach(view -> view.setFieldOptions(optionGroup.get(view.getFieldId())));
        }
    }
}
