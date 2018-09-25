package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.PageSchemeLineDTO;
import io.choerodon.issue.api.service.PageSchemeLineService;
import io.choerodon.issue.domain.PageSchemeLine;
import io.choerodon.issue.infra.enums.PageSchemeLineType;
import io.choerodon.issue.infra.mapper.PageMapper;
import io.choerodon.issue.infra.mapper.PageSchemeLineMapper;
import io.choerodon.issue.infra.mapper.PageSchemeMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/23
 */
@Component
@RefreshScope
public class PageSchemeLineServiceImpl extends BaseServiceImpl<PageSchemeLine> implements PageSchemeLineService {

    @Autowired
    private PageSchemeMapper pageSchemeMapper;

    @Autowired
    private PageSchemeLineMapper pageSchemeLineMapper;

    @Autowired
    private PageMapper pageMapper;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public PageSchemeLineDTO queryById(Long organizationId, Long pageSchemeLineId) {
        PageSchemeLine pageSchemeLine = pageSchemeLineMapper.selectByPrimaryKey(pageSchemeLineId);
        if (pageSchemeLine != null) {
            PageSchemeLineDTO line = modelMapper.map(pageSchemeLine, PageSchemeLineDTO.class);
            if (line.getType().equals(PageSchemeLineType.DEFAULT.value())) {
                line.setDefault(true);
            }
            return line;
        }
        return null;
    }

    @Override
    public PageSchemeLineDTO create(Long organizationId, PageSchemeLineDTO pageSchemeLineDTO) {

        if (pageMapper.selectByPrimaryKey(pageSchemeLineDTO.getPageId()) == null) {
            throw new CommonException("error.page.notFound");
        }

        if (pageSchemeMapper.selectByPrimaryKey(pageSchemeLineDTO.getSchemeId()) == null) {
            throw new CommonException("error.pageScheme.notFound");
        }

        if (!PageSchemeLineType.contain(pageSchemeLineDTO.getType())) {
            throw new CommonException("error.pageSchemeLine.type.illegal");
        }

        checkUniqueType(organizationId, pageSchemeLineDTO.getSchemeId(), pageSchemeLineDTO.getType());

        pageSchemeLineDTO.setOrganizationId(organizationId);
        PageSchemeLine pageSchemeLine = modelMapper.map(pageSchemeLineDTO, PageSchemeLine.class);
        if (pageSchemeLineMapper.insert(pageSchemeLine) != 1) {
            throw new CommonException("error.pageSchemeLine.create");
        }
        return queryById(organizationId, pageSchemeLine.getId());
    }

    @Override
    public PageSchemeLineDTO update(Long organizationId, PageSchemeLineDTO pageSchemeLineDTO) {

        if (pageSchemeLineDTO.getType() != null) {
            checkUniqueType(organizationId, pageSchemeLineDTO.getSchemeId(), pageSchemeLineDTO.getType());
        }

        PageSchemeLine pageSchemeLine = modelMapper.map(pageSchemeLineDTO, PageSchemeLine.class);
        int isUpdate = pageSchemeLineMapper.updateByPrimaryKeySelective(pageSchemeLine);
        if (isUpdate != 1) {
            throw new CommonException("error.pageSchemeLine.update");
        }
        return queryById(organizationId, pageSchemeLine.getId());
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long pageSchemeLineId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        PageSchemeLine pageSchemeLine = pageSchemeLineMapper.selectByPrimaryKey(pageSchemeLineId);
        if (pageSchemeLine == null) {
            throw new CommonException("error.pageSchemeLine.noFund");
        }
        if (!pageSchemeLine.getOrganizationId().equals(organizationId)) {
            throw new CommonException("error.pageSchemeLine.illegal");
        }
        //判断要删除的pageSchemeLine是否有使用中的项目toDo


        return result;
    }

    @Override
    public Boolean delete(Long organizationId, Long pageSchemeLineId) {
        Map<String, Object> result = checkDelete(organizationId, pageSchemeLineId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = pageSchemeLineMapper.deleteByPrimaryKey(pageSchemeLineId);
            if (isDelete != 1) {
                throw new CommonException("error.pageSchemeLine.delete");
            }
            //关联删除一些东西toDo
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void checkUniqueType(Long organizationId, Long schemeId, String type) {
        PageSchemeLine pageSchemeLine = new PageSchemeLine();
        pageSchemeLine.setOrganizationId(organizationId);
        pageSchemeLine.setSchemeId(schemeId);
        pageSchemeLine.setType(type);
        if (!pageSchemeLineMapper.select(pageSchemeLine).isEmpty()) {
            throw new CommonException("error.pageSchemeLine.exist");
        }
    }

    @Override
    public Long getPageIdByPageType(Long schemeId, String pageType) {
        PageSchemeLine line = new PageSchemeLine();
        line.setSchemeId(schemeId);
        line.setType(pageType);
        List<PageSchemeLine> lines = pageSchemeLineMapper.select(line);
        if(lines.isEmpty()){
            line.setType(PageSchemeLineType.DEFAULT.value());
            lines = pageSchemeLineMapper.select(line);
            if(lines.isEmpty()){
                return null;
            }
        }
        return lines.get(0).getPageId();
    }
}
