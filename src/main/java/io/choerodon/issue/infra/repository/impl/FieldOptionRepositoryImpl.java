package io.choerodon.issue.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.infra.dto.FieldOption;
import io.choerodon.issue.infra.mapper.FieldOptionMapper;
import io.choerodon.issue.infra.repository.FieldOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
@Component
public class FieldOptionRepositoryImpl implements FieldOptionRepository {
    @Autowired
    private FieldOptionMapper fieldOptionMapper;

    private static final String ERROR_OPTION_ILLEGAL = "error.option.illegal";
    private static final String ERROR_OPTION_CREATE = "error.option.create";
    private static final String ERROR_OPTION_DELETE = "error.option.delete";
    private static final String ERROR_OPTION_NOTFOUND = "error.option.notFound";
    private static final String ERROR_OPTION_UPDATE = "error.option.update";

    @Override
    public FieldOption create(FieldOption option) {
        if (fieldOptionMapper.insert(option) != 1) {
            throw new CommonException(ERROR_OPTION_CREATE);
        }
        return fieldOptionMapper.selectByPrimaryKey(option.getId());
    }

    @Override
    public void delete(Long optionId) {
        if (fieldOptionMapper.deleteByPrimaryKey(optionId) != 1) {
            throw new CommonException(ERROR_OPTION_DELETE);
        }
    }

    @Override
    public void update(FieldOption option) {
        if (fieldOptionMapper.updateByPrimaryKeySelective(option) != 1) {
            throw new CommonException(ERROR_OPTION_UPDATE);
        }
    }

    @Override
    public FieldOption queryById(Long organizationId, Long optionId) {
        FieldOption option = fieldOptionMapper.selectByPrimaryKey(optionId);
        if (option == null) {
            throw new CommonException(ERROR_OPTION_NOTFOUND);
        }
        if (!option.getOrganizationId().equals(organizationId)) {
            throw new CommonException(ERROR_OPTION_ILLEGAL);
        }
        return option;
    }
}
