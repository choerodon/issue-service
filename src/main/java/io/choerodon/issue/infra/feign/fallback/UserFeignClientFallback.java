package io.choerodon.issue.infra.feign.fallback;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.infra.feign.UserFeignClient;
import io.choerodon.issue.infra.feign.vo.ProjectVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public ResponseEntity<ProjectVO> queryProject(Long projectId) {
        throw new CommonException("error.userFeign.queryProject");
    }

    @Override
    public ResponseEntity<PageInfo<ProjectVO>> queryProjectsByOrgId(Long organizationId, Integer page, Integer size) {
        throw new CommonException("error.iamServiceFeignFallback.queryProjectsByOrgId");
    }
}


