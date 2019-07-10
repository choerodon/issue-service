package io.choerodon.issue.infra.repository;

import io.choerodon.issue.infra.dto.FieldDataLogDTO;

/**
 * @author shinan.chen
 * @since 2019/6/19
 */
public interface FieldDataLogRepository {
    FieldDataLogDTO create(FieldDataLogDTO create);
}
