package io.choerodon.issue.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.infra.feign.AgileFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2018/11/21
 */
@Component
public class AgileFeignClientFallback implements AgileFeignClient {
    @Override
    public ResponseEntity<Map<String, Object>> checkDeleteNode(Long organizationId, Long statusId, Map<Long, List<Long>> issueTypeIdsMap) {
        throw new CommonException("error.agileFeignClientFallback.checkDeleteNode");
    }
}


