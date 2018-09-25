package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.FieldOptionDTO;
import io.choerodon.issue.api.service.FieldOptionService;
import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.FieldOption;
import io.choerodon.issue.infra.mapper.FieldMapper;
import io.choerodon.issue.infra.mapper.FieldOptionMapper;
import io.choerodon.issue.infra.utils.ListChangeUtil;
import io.choerodon.issue.infra.utils.LoopUtil;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;


@Component
@RefreshScope
public class FieldOptionServiceImpl extends BaseServiceImpl<FieldOption> implements FieldOptionService {
    private static final String TRUE = "1";
    private static final String FALSE = "0";
    @Autowired
    private FieldOptionMapper fieldOptionMapper;
    @Autowired
    private FieldMapper fieldMapper;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    @Transactional
    public Boolean delete(Long fieldId, Long fieldOptionId) {
        Map<String, Object> result = checkDelete(fieldId, fieldOptionId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        List<Long> allChildIds = (List<Long>) result.get("allChildIds");
        if (canDelete) {
            for (Long id : allChildIds) {
                int isDelete = fieldOptionMapper.deleteByPrimaryKey(fieldOptionId);
                if (isDelete != 1) {
                    throw new CommonException("error.state.delete");
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public Boolean checkValue(Long fieldId, String value, Long id, Long parentId) {
        FieldOption select = new FieldOption();
        select.setFieldId(fieldId);
        select.setValue(value);
        select.setParentId(parentId==null?0L:parentId);
        select = fieldOptionMapper.selectOne(select);
        List<Field> xx =  fieldMapper.selectAll();
        List<FieldOption> xs = fieldOptionMapper.selectAll();
        if (select != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验返回false
            return select.getId().equals(id);
        }
        return true;
    }

    @Override
    public Map<String, Object> checkDelete(Long fieldId, Long fieldOptionId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        List<Long> allChildIds = getAllChildIds(fieldId, fieldOptionId);
        result.put("allChildIds", allChildIds);
        for (Long id : allChildIds) {
            FieldOption fieldOption = fieldOptionMapper.selectByPrimaryKey(fieldOptionId);
            if (fieldOption == null) {
                throw new CommonException("error.base.notFound");
            }
            //校验是否有issue用到这个FieldOption的值【todo】
        }
        return result;
    }

    /**
     * 获取当前fieldOptionId及其所有子Id的集合
     *
     * @param fieldId
     * @param fieldOptionId
     * @return
     */
    public List<Long> getAllChildIds(Long fieldId, Long fieldOptionId) {
        List<Long> allChildIds = new ArrayList<>();
        List<FieldOption> fieldOptions = fieldOptionMapper.queryByFieldId(fieldId, null);
        Map<Long, List<FieldOption>> map = fieldOptions.stream().collect(Collectors.groupingBy(FieldOption::getParentId));
        getChildLoop(allChildIds, fieldOptionId, map);
        return allChildIds;
    }

    public void getChildLoop(List<Long> ids, Long fieldOptionParentId, Map<Long, List<FieldOption>> map) {
        ids.add(fieldOptionParentId);
        List<FieldOption> fieldOptions = map.get(fieldOptionParentId);
        if (fieldOptions != null) {
            for (FieldOption fieldOption : fieldOptions) {
                getChildLoop(ids, fieldOption.getId(), map);
            }
        }
    }


    @Override
    @Transactional
    public void updateFieldOptionLoop(Long organizationId, Long fieldId, Long parentId, List<FieldOptionDTO> fieldOptionDTOS) {
        List<FieldOption> newFieldOption = modelMapper.map(fieldOptionDTOS, new TypeToken<List<FieldOption>>() {
        }.getType());
        List<FieldOption> oldFieldOption = fieldOptionMapper.queryByFieldId(fieldId, parentId);

        //获取减少的对象,进行删除
        BiPredicate<FieldOption, FieldOption> myEquals = (FieldOption x1, FieldOption x2) -> {
            if (x1.getId() == null || !x1.getId().equals(x2.getId())) {
                return false;
            }
            //x1是新的，x2是旧的，返回true的话代表有新的中有个等于旧的，则返回true，这个x2就是减少的
            return true;
        };
        List<FieldOption> reduce = ListChangeUtil.getReduceList(newFieldOption, oldFieldOption, myEquals);
        for (FieldOption red : reduce) {
            if (!delete(fieldId, red.getId())) {
                throw new CommonException("error.fieldOption.updateFieldOption.delete");
            }
        }

        int sequence = 1;
        for (FieldOptionDTO fieldOptionDTO : fieldOptionDTOS) {
            fieldOptionDTO.setParentId(parentId);
            fieldOptionDTO.setFieldId(fieldId);

            if (fieldOptionDTO.getValue() == null || fieldOptionDTO.getValue().equals("")) {
                throw new CommonException("error.fieldOption.updateFieldOption.value.null");
            }
            if (!checkValue(fieldOptionDTO.getFieldId(), fieldOptionDTO.getValue(), fieldOptionDTO.getId(), fieldOptionDTO.getParentId())) {
                throw new CommonException("error.fieldOption.updateFieldOption.value.exist");
            }

            fieldOptionDTO.setSequence(BigDecimal.valueOf(sequence));
            if (fieldOptionDTO.getId() != null && !fieldOptionDTO.getId().equals(0L)) {
                //编辑
                FieldOption fieldOption = modelMapper.map(fieldOptionDTO, FieldOption.class);
                if (fieldOptionMapper.updateByPrimaryKeySelective(fieldOption) != 1) {
                    throw new CommonException("error.fieldOption.update");
                }
            } else {
                //创建
                FieldOption fieldOption = modelMapper.map(fieldOptionDTO, FieldOption.class);
                fieldOption.setId(null);
                if (fieldOption.getIsDefault() == null) {
                    fieldOption.setIsDefault(FALSE);
                }
                if (fieldOption.getIsEnable() == null) {
                    fieldOption.setIsEnable(TRUE);
                }
                if (fieldOption.getParentId() == null) {
                    fieldOption.setParentId(0L);
                }
                fieldOptionMapper.insert(fieldOption);
                fieldOptionDTO.setId(fieldOption.getId());
            }
            sequence++;

            //更新子类
            List<FieldOptionDTO> childrens = fieldOptionDTO.getChildren();
            if (childrens != null && !childrens.isEmpty()) {
                updateFieldOptionLoop(organizationId, fieldId, fieldOptionDTO.getId(), childrens);
            }
        }
    }

    @Override
    public List<FieldOptionDTO> queryByFieldId(Long organizationId, Long fieldId) {
        List<FieldOption> fieldOptions = fieldOptionMapper.queryByFieldId(fieldId, null);
        List<FieldOptionDTO> fieldOptionDTOS = modelMapper.map(fieldOptions, new TypeToken<List<FieldOptionDTO>>() {
        }.getType());
        Map<Long, List<FieldOptionDTO>> map = fieldOptionDTOS.stream().collect(Collectors.groupingBy(FieldOptionDTO::getParentId));
        List<FieldOptionDTO> parent = LoopUtil.queryFieldOptionChild(0L, map);
        return parent;
    }
}
