package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.FieldDTO;
import io.choerodon.issue.api.dto.PageDTO;
import io.choerodon.issue.api.dto.PageDetailDTO;
import io.choerodon.issue.api.dto.PageSchemeDTO;
import io.choerodon.issue.api.service.PageService;
import io.choerodon.issue.domain.Field;
import io.choerodon.issue.domain.Page;
import io.choerodon.issue.domain.PageFieldRef;
import io.choerodon.issue.domain.PageScheme;
import io.choerodon.issue.infra.mapper.FieldMapper;
import io.choerodon.issue.infra.mapper.PageFieldRefMapper;
import io.choerodon.issue.infra.mapper.PageMapper;
import io.choerodon.issue.infra.mapper.PageSchemeMapper;
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

/**
 * @author shinan.chen
 * @Date 2018/8/22
 */
@Component
@RefreshScope
public class PageServiceImpl extends BaseServiceImpl<Page> implements PageService {

    private PageMapper pageMapper;

    private PageFieldRefMapper pageFieldRefMapper;

    private FieldMapper fieldMapper;

    @Autowired
    private PageSchemeMapper pageSchemeMapper;

    private final ModelMapper modelMapper = new ModelMapper();

    public PageServiceImpl(PageMapper pageMapper,
                           PageFieldRefMapper pageFieldRefMapper,
                           FieldMapper fieldMapper) {
        this.pageMapper = pageMapper;
        this.pageFieldRefMapper = pageFieldRefMapper;
        this.fieldMapper = fieldMapper;
    }

    @Override
    public PageDetailDTO queryById(Long organizationId, Long pageId) {
        Page page = pageMapper.selectByPrimaryKey(pageId);
        if (page != null) {
            PageDetailDTO pageDetailDTO = modelMapper.map(page, PageDetailDTO.class);
            //根据页面行表获取 自定义字段
            List<Field> fields = fieldMapper.queryByPageId(organizationId, pageId);
            pageDetailDTO.setFieldDTOs(modelMapper.map(fields, new TypeToken<List<FieldDTO>>() {
            }.getType()));
            return pageDetailDTO;
        }
        return null;
    }

    @Override
    @Transactional
    public PageDetailDTO create(Long organizationId, PageDetailDTO pageDTO) {

        if (!checkName(organizationId, pageDTO.getName(), null)) {
            throw new CommonException("error.page.name.exist");
        }

        pageDTO.setOrganizationId(organizationId);
        Page page = modelMapper.map(pageDTO, Page.class);
        if (pageMapper.insert(page) != 1) {
            throw new CommonException("error.issueType.create");
        }
        //创建页面字段配置
        createFields(organizationId, page.getId(), pageDTO.getFieldDTOs());

        return queryById(organizationId, page.getId());
    }

    @Override
    @Transactional
    public PageDetailDTO update(Long organizationId, PageDetailDTO pageDTO) {

        if (pageDTO.getName() != null && !checkName(organizationId, pageDTO.getName(), pageDTO.getId())) {
            throw new CommonException("error.page.name.exist");
        }

        Page page = modelMapper.map(pageDTO, Page.class);
        int isUpdate = pageMapper.updateByPrimaryKeySelective(page);
        if (isUpdate != 1) {
            throw new CommonException("error.page.update");
        }
        //更新页面字段配置,等待校验【toDo】
        pageFieldRefMapper.deleteByPageId(organizationId, pageDTO.getId());
        createFields(organizationId, pageDTO.getId(), pageDTO.getFieldDTOs());

        return queryById(organizationId, page.getId());
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long pageId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        Page page = pageMapper.selectByPrimaryKey(pageId);
        if (page == null) {
            throw new CommonException("error.page.noFund");
        }
        if (!page.getOrganizationId().equals(organizationId)) {
            throw new CommonException("error.page.illegal");
        }

        //判断是否关联页面方案
        List<PageScheme> pageSchemes = pageSchemeMapper.queryByPageId(organizationId, pageId);
        if (pageSchemes != null && !pageSchemes.isEmpty()) {
            result.put("pageSchemes", pageSchemes);
            result.put("canDelete", false);
        }

        //判断要删除的page是否有使用中的项目【toDo】
        return result;
    }

    @Override
    public Boolean delete(Long organizationId, Long pageId) {
        Map<String, Object> result = checkDelete(organizationId, pageId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = pageMapper.deleteByPrimaryKey(pageId);
            if (isDelete != 1) {
                throw new CommonException("error.page.delete");
            }
            pageFieldRefMapper.deleteByPageId(organizationId, pageId);
            //关联删除一些东西【toDo】
        } else {
            return false;
        }
        return true;
    }

    @Override
    public io.choerodon.core.domain.Page<PageDTO> pageQuery(Long organizationId, PageRequest pageRequest, PageDTO pageDTO, String param) {
        Page page = modelMapper.map(pageDTO, Page.class);

        io.choerodon.core.domain.Page<Page> pages = PageHelper.doPageAndSort(pageRequest,
                () -> pageMapper.fulltextSearch(page, param));
        List<Page> content = pages.getContent();
        List<PageDTO> contentDTO = modelMapper.map(content, new TypeToken<List<PageDTO>>() {
        }.getType());

        //关联页面方案，及是否可删除
        for (PageDTO pd : contentDTO) {
            Map<String, Object> result = checkDelete(organizationId, pd.getId());
            pd.setCanDelete((Boolean) result.get("canDelete"));
            pd.setPageSchemeDTOS(result.get("pageSchemes") != null ? modelMapper.map(result.get("pageSchemes"), new TypeToken<List<PageSchemeDTO>>() {
            }.getType()) : null);
        }

        io.choerodon.core.domain.Page<PageDTO> pagesDTO = new io.choerodon.core.domain.Page<>();
        pagesDTO.setNumber(pages.getNumber());
        pagesDTO.setNumberOfElements(pages.getNumberOfElements());
        pagesDTO.setSize(pages.getSize());
        pagesDTO.setTotalElements(pages.getTotalElements());
        pagesDTO.setTotalPages(pages.getTotalPages());
        pagesDTO.setContent(contentDTO);
        return pagesDTO;
    }

    @Override
    public List<PageDTO> listQuery(PageDTO pageDTO, String param) {
        Page page = modelMapper.map(pageDTO, Page.class);
        List<Page> pageList = pageMapper.fulltextSearch(page, param);
        return modelMapper.map(pageList, new TypeToken<List<PageDTO>>() {
        }.getType());
    }

    @Override
    public Boolean checkName(Long organizationId, String name, Long id) {
        Page select = new Page();
        select.setName(name);
        select.setOrganizationId(organizationId);
        select = pageMapper.selectOne(select);
        if (select != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验返回false
            return select.getId().equals(id);
        }
        return true;
    }

    @Override
    public void createFields(Long organizationId, Long pageId, List<FieldDTO> fieldDTOs) {
        if (fieldDTOs != null && !fieldDTOs.isEmpty()) {
            int sequence = 0;
            for (FieldDTO fieldDTO : fieldDTOs) {
                if (fieldMapper.selectByPrimaryKey(fieldDTO.getId()) != null) {
                    PageFieldRef ref = new PageFieldRef();
                    ref.setFieldId(fieldDTO.getId());
                    ref.setPageId(pageId);
                    ref.setSequence(BigDecimal.valueOf(sequence));
                    ref.setOrganizationId(organizationId);
                    pageFieldRefMapper.insert(ref);
                } else {
                    throw new CommonException("error.field.notFound");
                }
                sequence++;
            }
        }
    }
}
