package io.choerodon.issue.infra.repository;

import io.choerodon.issue.infra.dto.PageFieldDTO;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface PageFieldRepository {
    PageFieldDTO create(PageFieldDTO pageField);

    void delete(Long pageFieldId);

    void update(PageFieldDTO pageField);

    PageFieldDTO queryById(Long organizationId, Long projectId, Long pageFieldId);
}
