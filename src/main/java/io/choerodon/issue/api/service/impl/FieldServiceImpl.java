package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.FieldDTO;
import io.choerodon.issue.api.dto.FieldDetailDTO;
import io.choerodon.issue.api.dto.FieldOptionDTO;
import io.choerodon.issue.api.service.FieldConfigLineService;
import io.choerodon.issue.api.service.FieldOptionService;
import io.choerodon.issue.api.service.FieldService;
import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.FieldOption;
import io.choerodon.issue.domain.PageFieldRef;
import io.choerodon.issue.infra.enums.FieldType;
import io.choerodon.issue.infra.mapper.*;
import io.choerodon.issue.infra.utils.ProjectUtil;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author jiameng.cao
 * @Date 2018/8/21
 */
@Component
@RefreshScope
public class FieldServiceImpl extends BaseServiceImpl<Field> implements FieldService {

    public static final String YES = "1";

    @Autowired
    private FieldMapper fieldMapper;
    @Autowired
    private FieldConfigLineMapper fieldConfigLineMapper;
    @Autowired
    FieldConfigLineService fieldConfigLineService;
    @Autowired
    private FieldOptionService fieldOptionService;
    @Autowired
    private FieldOptionMapper fieldOptionMapper;
    @Autowired
    private PageFieldRefMapper pageFieldRefMapper;
    @Autowired
    private PageMapper pageMapper;
    @Autowired
    private ProjectUtil projectUtil;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public Page<FieldDTO> pageQuery(Long organizationId, PageRequest pageRequest, FieldDTO fieldDTO, String params) {
        Field field = modelMapper.map(fieldDTO, Field.class);
        Page<Field> pages = PageHelper.doPageAndSort(pageRequest,
                () -> fieldMapper.fulltextSearch(field, params));
        List<FieldDTO> contentDTO = modelMapper.map(pages.getContent(), new TypeToken<List<FieldDTO>>() {
        }.getType());

        //判断字段是否可删除
        for (FieldDTO fd : contentDTO) {
            Map<String, Object> result = checkDelete(organizationId, fd.getId());
            fd.setCanDelete((Boolean) result.get("canDelete"));
        }

        Page<FieldDTO> pagesDTO = new Page<>();
        pagesDTO.setNumber(pages.getNumber());
        pagesDTO.setNumberOfElements(pages.getNumberOfElements());
        pagesDTO.setSize(pages.getSize());
        pagesDTO.setTotalElements(pages.getTotalElements());
        pagesDTO.setTotalPages(pages.getTotalPages());
        pagesDTO.setContent(contentDTO);
        return pagesDTO;
    }

    @Override
    public List<FieldDTO> listQuery(Long organizationId,FieldDTO fieldDTO, String params) {
        fieldDTO.setOrganizationId(organizationId);
        Field field = modelMapper.map(fieldDTO, Field.class);
        List<Field> fieldList = fieldMapper.fulltextSearch(field, params);
        return modelMapper.map(fieldList, new TypeToken<List<FieldDTO>>() {
        }.getType());
    }

    @Override
    @Transactional
    public FieldDTO create(Long organizationId, FieldDTO fieldDTO) {
        if (!checkName(organizationId, fieldDTO.getName(), null)) {
            throw new CommonException("error.field.checkName");
        }

        if (!FieldType.contain(fieldDTO.getType())) {
            throw new CommonException("error.field.type.illegal");
        }

        fieldDTO.setOrganizationId(organizationId);
        Field field = modelMapper.map(fieldDTO, Field.class);
        if (fieldMapper.insert(field) != 1) {
            throw new CommonException("error.field.create");
        }
        field = fieldMapper.selectByPrimaryKey(field.getId());

        fieldConfigLineService.createConfigLine(organizationId, field);

        return modelMapper.map(field, FieldDTO.class);
    }

    @Override
    @Transactional
    public FieldDetailDTO update(FieldDetailDTO fieldDetailDTO) {
        if (fieldDetailDTO.getName() != null && !checkName(fieldDetailDTO.getOrganizationId(), fieldDetailDTO.getName(), fieldDetailDTO.getId())) {
            throw new CommonException("error.field.checkName");
        }

        if (!FieldType.contain(fieldDetailDTO.getType())) {
            throw new CommonException("error.field.type.illegal");
        }

        Field field = modelMapper.map(fieldDetailDTO, Field.class);
        int isUpdate = fieldMapper.updateByPrimaryKey(field);
        if (isUpdate != 1) {
            throw new CommonException("error.field.update");
        }

        //更新字段值
        if (fieldDetailDTO.getFieldOptions() != null) {
            fieldOptionService.updateFieldOptionLoop(fieldDetailDTO.getOrganizationId(), fieldDetailDTO.getId(), 0L, fieldDetailDTO.getFieldOptions());
        }

        return queryById(fieldDetailDTO.getOrganizationId(), fieldDetailDTO.getId());
    }

    @Override
    public Boolean delete(Long organizationId, Long fieldId) {
        Map<String, Object> result = checkDelete(organizationId, fieldId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = fieldMapper.deleteByPrimaryKey(fieldId);
            if (isDelete != 1) {
                throw new CommonException("error.state.delete");
            }

            fieldOptionMapper.deleteByFieldId(fieldId);
        } else {
            return false;
        }
        //校验
        fieldConfigLineMapper.deleteByFieldId(fieldId);
        return true;
    }

    @Override
    public Boolean checkName(Long organizationId, String name, Long id) {
        Field select = new Field();
        select.setName(name);
        select.setOrganizationId(organizationId);
        select = fieldMapper.selectOne(select);
        if (select != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验返回false
            return select.getId().equals(id);
        }
        return true;
    }


    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long fieldId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        Field field = fieldMapper.selectByPrimaryKey(fieldId);
        if (field == null) {
            throw new CommonException("error.base.notFound");
        } else if (!field.getOrganizationId().equals(organizationId)) {
            throw new CommonException("error.field.illegal");
        }

        //判断字段是否关联页面
        List<Long> ids = queryRelatedPage(organizationId, fieldId);
        if (ids != null && !ids.isEmpty()) {
            result.put("canDelete", false);
        }

        //校验字段值是否有正在使用的issue
        List<FieldOption> fieldOptions = fieldOptionMapper.queryByFieldId(fieldId, null);
        for (FieldOption fieldOption : fieldOptions) {
            Map<String, Object> fr = fieldOptionService.checkDelete(fieldId, fieldOption.getId());
            Boolean canDelete = (Boolean) fr.get("canDelete");
            if (!canDelete) {
                result.put("canDelete", false);
            }
        }
        return result;
    }

    @Override
    public FieldDetailDTO queryById(Long organizationId, Long id) {
        Field field = fieldMapper.selectByPrimaryKey(id);
        if (field == null) {
            throw new CommonException("error.base.notFound");
        }
        FieldDetailDTO fieldDetailDTO = modelMapper.map(field, FieldDetailDTO.class);
        fieldDetailDTO.setFieldOptions(fieldOptionService.queryByFieldId(organizationId, id));
        return fieldDetailDTO;
    }

    @Override
    public List<Long> queryRelatedPage(Long organizationId, Long fieldId) {
        PageFieldRef ref = new PageFieldRef();
        ref.setFieldId(fieldId);
        ref.setOrganizationId(organizationId);
        return pageFieldRefMapper.select(ref).stream().map(PageFieldRef::getPageId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Long> updateRelatedPage(Long organizationId, Long fieldId, List<Long> pageIds) {
        if (fieldMapper.selectByPrimaryKey(fieldId) == null) {
            throw new CommonException("error.field.notFound");
        }
        PageFieldRef delete = new PageFieldRef();
        delete.setFieldId(fieldId);
        delete.setOrganizationId(organizationId);
        pageFieldRefMapper.delete(delete);

        for (Long pageId : pageIds) {
            if (pageMapper.selectByPrimaryKey(pageId) == null) {
                throw new CommonException("error.page.notFound");
            }
            PageFieldRef ref = new PageFieldRef();
            ref.setFieldId(fieldId);
            ref.setPageId(pageId);
            ref.setSequence(BigDecimal.valueOf(0));
            ref.setOrganizationId(organizationId);
            pageFieldRefMapper.insert(ref);
        }
        return pageIds;
    }

    @Override
    public String getDefaultValue(Long organizationId, Long fieldId) {
        Field field = fieldMapper.selectByPrimaryKey(fieldId);
        if (FieldType.hasOption(field.getType())) {
            List<FieldOptionDTO> dtos = fieldOptionService.queryByFieldId(organizationId, field.getId());
            if(dtos!=null){
                return dtos.stream().filter(x -> x.getIsDefault().equals(YES)).map(x -> x.getId().toString()).collect(Collectors.joining(","));
            }
            return null;
        } else {
            return field.getDefaultValue();
        }
    }
}