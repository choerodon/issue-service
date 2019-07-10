package io.choerodon.issue.app.service;

import io.choerodon.issue.api.vo.FieldDataLogCreateVO;
import io.choerodon.issue.api.vo.FieldDataLogVO;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/6/19
 */
public interface FieldDataLogService {

    FieldDataLogVO createDataLog(Long projectId, String schemeCode, FieldDataLogCreateVO create);

    void deleteByFieldId(Long projectId, Long fieldId);

    List<FieldDataLogVO> queryByInstanceId(Long projectId, Long instanceId, String schemeCode);
}
