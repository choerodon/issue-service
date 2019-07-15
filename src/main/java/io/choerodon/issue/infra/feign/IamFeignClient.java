package io.choerodon.issue.infra.feign;

import com.github.pagehelper.PageInfo;
import io.choerodon.issue.infra.feign.fallback.IamFeignClientFallback;
import io.choerodon.issue.infra.feign.vo.ProjectDTO;
import io.choerodon.issue.infra.feign.vo.UserVO;
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
    /**
     * 按照id查询项目
     *
     * @param projectId
     * @return
     */
    @GetMapping(value = "/v1/projects/{project_id}")
    ResponseEntity<ProjectDTO> queryProject(@PathVariable(name = "project_id") Long projectId);

    /**
     * 根据组织id查询所有项目
     *
     * @param organizationId
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/projects")
    ResponseEntity<PageInfo<ProjectDTO>> queryProjectsByOrgId(@PathVariable("organization_id") Long organizationId,
                                                              @RequestParam(value = "page", required = false) Integer page,
                                                              @RequestParam(value = "size", required = false) Integer size);

    @PostMapping(value = "/v1/users/ids")
    ResponseEntity<List<UserVO>> listUsersByIds(@RequestBody Long[] ids,
                                                @RequestParam(name = "only_enabled") Boolean onlyEnabled);

    @GetMapping(value = "/v1/projects/{project_id}")
    ResponseEntity<ProjectDTO> queryProjectInfo(@PathVariable(name = "project_id") Long projectId);
}
