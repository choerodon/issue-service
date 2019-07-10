package io.choerodon.issue.infra.repository;

import io.choerodon.issue.infra.dto.FieldDataLog;

/**
 * @author shinan.chen
 * @since 2019/6/19
 */
public interface FieldDataLogRepository {
    FieldDataLog create(FieldDataLog create);
}
