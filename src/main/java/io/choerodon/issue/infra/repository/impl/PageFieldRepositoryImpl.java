package io.choerodon.issue.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.infra.dto.PageField;
import io.choerodon.issue.infra.mapper.PageFieldMapper;
import io.choerodon.issue.infra.repository.PageFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
@Component
public class PageFieldRepositoryImpl implements PageFieldRepository {
    @Autowired
    private PageFieldMapper pageFieldMapper;

    private static final String ERROR_PAGEFIELD_CREATE = "error.pageField.create";
    private static final String ERROR_PAGEFIELD_DELETE = "error.pageField.delete";
    private static final String ERROR_PAGEFIELD_NOTFOUND = "error.pageField.notFound";
    private static final String ERROR_PAGEFIELD_UPDATE = "error.pageField.update";

    @Override
    public PageField create(PageField field) {
        if (pageFieldMapper.insert(field) != 1) {
            throw new CommonException(ERROR_PAGEFIELD_CREATE);
        }
        return pageFieldMapper.selectByPrimaryKey(field.getId());
    }

    @Override
    public void delete(Long fieldId) {
        if (pageFieldMapper.deleteByPrimaryKey(fieldId) != 1) {
            throw new CommonException(ERROR_PAGEFIELD_DELETE);
        }
    }

    @Override
    public void update(PageField pageField) {
        if (pageFieldMapper.updateByPrimaryKeySelective(pageField) != 1) {
            throw new CommonException(ERROR_PAGEFIELD_UPDATE);
        }
    }

    @Override
    public PageField queryById(Long organizationId, Long projectId, Long pageFieldId) {
        PageField pageField = pageFieldMapper.selectByPrimaryKey(pageFieldId);
        if (pageField == null) {
            throw new CommonException(ERROR_PAGEFIELD_NOTFOUND);
        }
        return pageField;
    }
}
