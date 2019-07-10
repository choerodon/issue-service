package io.choerodon.issue.infra.repository;

import io.choerodon.issue.infra.dto.FieldOptionDTO;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface FieldOptionRepository {
    FieldOptionDTO create(FieldOptionDTO option);

    void delete(Long optionId);

    void update(FieldOptionDTO option);

    FieldOptionDTO queryById(Long organizationId, Long optionId);
}
