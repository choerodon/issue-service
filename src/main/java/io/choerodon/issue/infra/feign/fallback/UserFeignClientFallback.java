package io.choerodon.issue.infra.feign.fallback;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.ProjectDTO;
import io.choerodon.issue.infra.feign.UserFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public ResponseEntity<ProjectDTO> queryProject(Long projectId) {
        throw new CommonException("error.userFeign.queryProject");
    }

    @Override
    public ResponseEntity<PageInfo<ProjectDTO>> queryProjectsByOrgId(Long organizationId, Integer page, Integer size) {
        throw new CommonException("error.iamServiceFeignFallback.queryProjectsByOrgId");
    }
}


