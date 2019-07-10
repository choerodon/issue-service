package io.choerodon.issue.infra.feign;

import io.choerodon.issue.infra.feign.vo.ProjectVO;
import io.choerodon.issue.infra.feign.vo.UserVO;
import io.choerodon.issue.infra.feign.fallback.IamFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/6/11
 */
@FeignClient(value = "iam-service", fallback = IamFeignClientFallback.class)
public interface IamFeignClient {
    @PostMapping(value = "/v1/users/ids")
    ResponseEntity<List<UserVO>> listUsersByIds(@RequestBody Long[] ids,
                                                @RequestParam(name = "only_enabled") Boolean onlyEnabled);

    @GetMapping(value = "/v1/projects/{project_id}")
    ResponseEntity<ProjectVO> queryProjectInfo(@PathVariable(name = "project_id") Long projectId);
}
