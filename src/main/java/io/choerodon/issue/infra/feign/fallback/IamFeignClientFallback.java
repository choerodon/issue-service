package io.choerodon.issue.infra.feign.fallback;

import io.choerodon.core.exception.FeignException;
import io.choerodon.issue.infra.feign.IamFeignClient;
import io.choerodon.issue.infra.feign.vo.ProjectVO;
import io.choerodon.issue.infra.feign.vo.UserVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/6/11
 */
@Component
public class IamFeignClientFallback implements IamFeignClient {
    private static final String BATCH_QUERY_ERROR = "error.UserFeign.queryList";
    private static final String PROJECT_INFO_ERROR = "error.UserFeign.queryProjectInfo";

    @Override
    public ResponseEntity<List<UserVO>> listUsersByIds(Long[] ids, Boolean onlyEnabled) {
        throw new FeignException(BATCH_QUERY_ERROR);
    }

    @Override
    public ResponseEntity<ProjectVO> queryProjectInfo(Long projectId) {
        throw new FeignException(PROJECT_INFO_ERROR);
    }
}
