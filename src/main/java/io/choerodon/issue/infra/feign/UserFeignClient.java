package io.choerodon.issue.infra.feign;

import com.github.pagehelper.PageInfo;
import io.choerodon.issue.infra.feign.vo.ProjectVO;
import io.choerodon.issue.infra.feign.fallback.UserFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author peng.jiang
 */
@FeignClient(value = "iam-service", fallback = UserFeignClientFallback.class)
@Component
public interface UserFeignClient {
    /**
     * 按照id查询项目
     *
     * @param projectId
     * @return
     */
    @GetMapping(value = "/v1/projects/{project_id}")
    ResponseEntity<ProjectVO> queryProject(@PathVariable(name = "project_id") Long projectId);

    /**
     * 根据组织id查询所有项目
     *
     * @param organizationId
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/projects")
    ResponseEntity<PageInfo<ProjectVO>> queryProjectsByOrgId(@PathVariable("organization_id") Long organizationId,
                                                             @RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "size", required = false) Integer size);
}
