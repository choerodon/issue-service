package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.FieldConfigSchemeDTO;
import io.choerodon.issue.api.dto.FieldConfigSchemeDetailDTO;
import io.choerodon.issue.api.dto.FieldConfigSchemeLineDTO;
import io.choerodon.issue.api.service.FieldConfigSchemeLineService;
import io.choerodon.issue.api.service.FieldConfigSchemeService;
import io.choerodon.issue.domain.FieldConfigScheme;
import io.choerodon.issue.infra.mapper.FieldConfigSchemeLineMapper;
import io.choerodon.issue.infra.mapper.FieldConfigSchemeMapper;
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
 * @date 2018/8/27
 */

@Component
@RefreshScope
public class FieldConfigSchemeServiceImpl extends BaseServiceImpl<FieldConfigScheme> implements FieldConfigSchemeService {
    @Autowired
    private FieldConfigSchemeMapper fieldConfigSchemeMapper;
    @Autowired
    private FieldConfigSchemeLineMapper fieldConfigSchemeLineMapper;
    @Autowired
    FieldConfigSchemeLineService fieldConfigSchemeLineService;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public Page<FieldConfigSchemeDTO> pageQuery(PageRequest pageRequest, FieldConfigSchemeDTO fieldConfigSchemeDTO, String params) {
        FieldConfigScheme fieldConfigScheme = modelMapper.map(fieldConfigSchemeDTO, FieldConfigScheme.class);
        Page<FieldConfigScheme> pages = PageHelper.doPageAndSort(pageRequest,
                () -> fieldConfigSchemeMapper.fulltextSearch(fieldConfigScheme, params));

        Page<FieldConfigSchemeDTO> pagesDTO = new Page<>();
        pagesDTO.setNumber(pages.getNumber());
        pagesDTO.setNumberOfElements(pages.getNumberOfElements());
        pagesDTO.setSize(pages.getSize());
        pagesDTO.setTotalElements(pages.getTotalElements());
        pagesDTO.setTotalPages(pages.getTotalPages());
        pagesDTO.setContent(modelMapper.map(pages.getContent(), new TypeToken<List<FieldConfigSchemeDTO>>() {
        }.getType()));
        return pagesDTO;
    }

    @Override
    @Transactional
    public FieldConfigSchemeDetailDTO create(Long organizationId, FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO) {
        if (!checkName(organizationId, fieldConfigSchemeDetailDTO.getName(), null)) {
            throw new CommonException("error.fieldConfigScheme.name.exist");
        }

        fieldConfigSchemeDetailDTO.setOrganizationId(organizationId);
        FieldConfigScheme fieldConfigScheme = modelMapper.map(fieldConfigSchemeDetailDTO, FieldConfigScheme.class);
        if (fieldConfigSchemeMapper.insert(fieldConfigScheme) != 1) {
            throw new CommonException("error.fieldConfigScheme.create");
        }
        //创建方案配置
        createConfig(organizationId, fieldConfigScheme.getId(), fieldConfigSchemeDetailDTO.getFieldConfigSchemeLineDTOList());

        return queryById(organizationId, fieldConfigScheme.getId());
    }

    @Override
    public Boolean checkName(Long organizationId, String name, Long id) {
        FieldConfigScheme select = new FieldConfigScheme();
        select.setName(name);
        select.setOrganizationId(organizationId);
        select = fieldConfigSchemeMapper.selectOne(select);
        if (select != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验返回false
            return select.getId().equals(id);
        }
        return true;
    }

    @Override
    public void createConfig(Long organizationId, Long fieldConfigSchemeId, List<FieldConfigSchemeLineDTO> fieldConfigSchemeLineDTOList) {
        if (fieldConfigSchemeLineDTOList != null && !fieldConfigSchemeLineDTOList.isEmpty()) {
            for (FieldConfigSchemeLineDTO fieldConfigSchemeLineDTO : fieldConfigSchemeLineDTOList) {
                fieldConfigSchemeLineDTO.setSchemeId(fieldConfigSchemeId);
                fieldConfigSchemeLineService.create(organizationId, fieldConfigSchemeLineDTO);
            }
        } else {
            throw new CommonException("error.fieldConfigSchemeLine.null");
        }
    }

    @Override
    public FieldConfigSchemeDetailDTO queryById(Long organizationId, Long schemeId) {
        FieldConfigScheme fieldConfigScheme = fieldConfigSchemeMapper.selectByPrimaryKey(schemeId);
        if (fieldConfigScheme != null) {
            FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO = modelMapper.map(fieldConfigScheme, FieldConfigSchemeDetailDTO.class);
            //根据方案配置
            fieldConfigSchemeDetailDTO.setFieldConfigSchemeLineDTOList(modelMapper.map(fieldConfigSchemeLineMapper.queryBySchemeId(schemeId), new TypeToken<List<FieldConfigSchemeLineDTO>>() {
            }.getType()));
            return fieldConfigSchemeDetailDTO;
        }
        return null;
    }

    @Override
    @Transactional
    public FieldConfigSchemeDetailDTO update(Long organizationId, FieldConfigSchemeDetailDTO fieldConfigSchemeDetailDTO) {
        if (fieldConfigSchemeDetailDTO.getName() != null && !checkName(organizationId, fieldConfigSchemeDetailDTO.getName(), fieldConfigSchemeDetailDTO.getId())) {
            throw new CommonException("error.fieldConfigScheme.name.exist");
        }

        FieldConfigScheme fieldConfigScheme = modelMapper.map(fieldConfigSchemeDetailDTO, FieldConfigScheme.class);
        int isUpdate = fieldConfigSchemeMapper.updateByPrimaryKeySelective(fieldConfigScheme);
        if (isUpdate != 1) {
            throw new CommonException("error.fieldConfigScheme.update");
        }
        //更新方案配置,等待校验

        fieldConfigSchemeLineMapper.deleteBySchemeId(organizationId, fieldConfigSchemeDetailDTO.getId());
        createConfig(organizationId, fieldConfigScheme.getId(), fieldConfigSchemeDetailDTO.getFieldConfigSchemeLineDTOList());

        return queryById(organizationId, fieldConfigScheme.getId());
    }

    @Override
    public Boolean delete(Long organizationId, Long schemeId) {
        Map<String, Object> result = checkDelete(organizationId, schemeId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = fieldConfigSchemeMapper.deleteByPrimaryKey(schemeId);
            if (isDelete != 1) {
                throw new CommonException("error.state.delete");
            }
            fieldConfigSchemeLineMapper.deleteBySchemeId(organizationId, schemeId);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long schemeId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        FieldConfigScheme fieldConfigScheme = fieldConfigSchemeMapper.selectByPrimaryKey(schemeId);
        if (fieldConfigScheme == null) {
            throw new CommonException("error.fieldConfigScheme.notFound");
        }
        if (!fieldConfigScheme.getOrganizationId().equals(organizationId)) {
            throw new CommonException("error.fieldConfigScheme.illegal");
        }
        //判断要删除的pageScheme是否有使用中的项目toDo


        return result;
    }
}
