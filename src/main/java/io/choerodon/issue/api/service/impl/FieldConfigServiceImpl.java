package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.*;
import io.choerodon.issue.api.service.FieldConfigService;
import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.FieldConfig;
import io.choerodon.issue.domain.FieldConfigLine;
import io.choerodon.issue.domain.FieldConfigScheme;
import io.choerodon.issue.infra.mapper.FieldConfigLineMapper;
import io.choerodon.issue.infra.mapper.FieldConfigMapper;
import io.choerodon.issue.infra.mapper.FieldConfigSchemeMapper;
import io.choerodon.issue.infra.mapper.FieldMapper;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @date 2018/8/23
 */

@Component
@RefreshScope
public class FieldConfigServiceImpl extends BaseServiceImpl<FieldConfig> implements FieldConfigService {

    private static final String NO = "0";

    @Autowired
    private FieldConfigMapper fieldConfigMapper;
    @Autowired
    private FieldMapper fieldMapper;
    @Autowired
    private FieldConfigLineMapper fieldConfigLineMapper;
    @Autowired
    private FieldConfigSchemeMapper fieldConfigSchemeMapper;


    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public Page<FieldConfigDTO> pageQuery(Long organizationId, PageRequest pageRequest, FieldConfigDTO fieldConfigDTO, String params) {
        FieldConfig fieldConfig = modelMapper.map(fieldConfigDTO, FieldConfig.class);
        Page<FieldConfig> pages = PageHelper.doPageAndSort(pageRequest,
                () -> fieldConfigMapper.fulltextSearch(fieldConfig, params));
        List<FieldConfigDTO> contentDTO = modelMapper.map(pages.getContent(), new TypeToken<List<FieldConfigDTO>>() {
        }.getType());

        //关联字段配置方案，及是否可删除
        for (FieldConfigDTO fcd : contentDTO) {
            Map<String, Object> result = checkDelete(organizationId, fcd.getId());
            fcd.setCanDelete((Boolean) result.get("canDelete"));
            fcd.setFieldConfigSchemeDTOS(result.get("fieldConfigSchemes") != null ? modelMapper.map(result.get("fieldConfigSchemes"), new TypeToken<List<FieldConfigSchemeDTO>>() {
            }.getType()) : null);
        }

        Page<FieldConfigDTO> pagesDTO = new Page<>();
        pagesDTO.setNumber(pages.getNumber());
        pagesDTO.setNumberOfElements(pages.getNumberOfElements());
        pagesDTO.setSize(pages.getSize());
        pagesDTO.setTotalElements(pages.getTotalElements());
        pagesDTO.setTotalPages(pages.getTotalPages());
        pagesDTO.setContent(contentDTO);
        return pagesDTO;
    }

    @Override
    @Transactional
    public FieldConfigDTO create(Long organizationId, FieldConfigDTO fieldConfigDTO) {
        if (!checkName(organizationId, fieldConfigDTO.getName(), null)) {
            throw new CommonException("error.fieldConfig.checkName");
        }
        fieldConfigDTO.setOrganizationId(organizationId);
        FieldConfig fieldConfig = modelMapper.map(fieldConfigDTO, FieldConfig.class);
        if (fieldConfigMapper.insert(fieldConfig) != 1) {
            throw new CommonException("error.fieldConfig.create");
        }
        fieldConfig = fieldConfigMapper.selectByPrimaryKey(fieldConfig.getId());

        //创建字段配置后同时创建字段配置行表数据
        Field select;
        select = new Field();
        select.setOrganizationId(organizationId);
        List<Field> fields = fieldMapper.select(select);
        for (Field record : fields) {
            FieldConfigLine fieldConfigLine = new FieldConfigLine();
            fieldConfigLine.setFieldId(record.getId());
            fieldConfigLine.setFieldConfigId(fieldConfig.getId());
            fieldConfigLine.setIsDisplay(NO);
            fieldConfigLine.setIsRequired(NO);
            if (fieldConfigLineMapper.insert(fieldConfigLine) != 1) {
                throw new CommonException("error.fieldConfigLine.create");
            }
        }


        return modelMapper.map(fieldConfig, FieldConfigDTO.class);
    }

    @Override
    public Boolean checkName(Long organizationId, String name, Long id) {
        FieldConfig select = new FieldConfig();
        select.setName(name);
        select.setOrganizationId(organizationId);
        select = fieldConfigMapper.selectOne(select);
        if (select != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验返回false
            return select.getId().equals(id);
        }
        return true;
    }

    @Override
    public Boolean delete(Long organizationId, Long id) {
        List<FieldConfig> x = fieldConfigMapper.selectAll();
        Map<String, Object> result = checkDelete(organizationId, id);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = fieldConfigMapper.deleteByPrimaryKey(id);
            if (isDelete != 1) {
                throw new CommonException("error.state.delete");
            }
        } else {
            return false;
        }
        //校验
        fieldConfigLineMapper.deleteByFieldConfigId(id);
        return true;
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        FieldConfig fieldConfig = fieldConfigMapper.selectByPrimaryKey(id);
        if (fieldConfig == null) {
            throw new CommonException("error.base.notFound");
        } else if (!fieldConfig.getOrganizationId().equals(organizationId)) {
            throw new CommonException("error.fieldConfig.illegal");
        }

        //是否关联字段配置方案
        List<FieldConfigScheme> fieldConfigSchemes = fieldConfigSchemeMapper.queryByFieldConfigId(organizationId, id);
        if (fieldConfigSchemes != null && !fieldConfigSchemes.isEmpty()) {
            result.put("fieldConfigSchemes", fieldConfigSchemes);
            result.put("canDelete", false);
        }

        return result;
    }

    @Override
    public List<FieldConfigDTO> queryByOrgId(Long organizationId) {
        FieldConfig fieldConfig = new FieldConfig();
        fieldConfig.setOrganizationId(organizationId);
        List<FieldConfig> fieldConfigList = fieldConfigMapper.select(fieldConfig);
        return modelMapper.map(fieldConfigList, new TypeToken<List<FieldConfigDTO>>() {
        }.getType());
    }

    @Override
    public FieldConfigDetailDTO queryByFieldConfigId(Long organizationId, Long fieldConfigId) {
        FieldConfig fieldConfig = fieldConfigMapper.selectByPrimaryKey(fieldConfigId);
        Field field=new Field();
        List<Field> fieldList=fieldMapper.select(field);
        if (fieldConfig != null) {
            FieldConfigDetailDTO fieldConfigDetailDTO = modelMapper.map(fieldConfig, FieldConfigDetailDTO.class);

            fieldConfigDetailDTO.setFieldConfigLineDTOList(modelMapper.map(fieldConfigLineMapper.queryByFieldConfigId(fieldConfigId), new TypeToken<List<FieldConfigLineDetailDTO>>() {
            }.getType()));
            for (FieldConfigLineDetailDTO record:fieldConfigDetailDTO.getFieldConfigLineDTOList()){
                List<io.choerodon.issue.domain.Page> pageDTOS= fieldMapper.queryPageByFieldId(record.getFieldId());
                record.setPageDTOList(modelMapper.map(pageDTOS,new TypeToken<List<PageDTO>>(){}.getType()));
            }
            return fieldConfigDetailDTO;
        }
        return null;
    }

    @Override
    public FieldConfigDetailDTO update(FieldConfigDetailDTO fieldConfigDetailDTO) {
        if (fieldConfigDetailDTO.getName() != null && !checkName(fieldConfigDetailDTO.getOrganizationId(), fieldConfigDetailDTO.getName(), fieldConfigDetailDTO.getId())) {
            throw new CommonException("error.fieldConfig.checkName");
        }

        FieldConfig fieldConfig = modelMapper.map(fieldConfigDetailDTO, FieldConfig.class);
        int isUpdate = fieldConfigMapper.updateByPrimaryKey(fieldConfig);
        if (isUpdate != 1) {
            throw new CommonException("error.fieldConfig.update");
        }

        return queryByFieldConfigId(fieldConfigDetailDTO.getOrganizationId(), fieldConfigDetailDTO.getId());
    }
}
