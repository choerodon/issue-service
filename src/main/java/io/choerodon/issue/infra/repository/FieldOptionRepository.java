package io.choerodon.issue.infra.repository;

import io.choerodon.issue.infra.dto.FieldOption;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface FieldOptionRepository {
    FieldOption create(FieldOption option);

    void delete(Long optionId);

    void update(FieldOption option);

    FieldOption queryById(Long organizationId, Long optionId);
}
