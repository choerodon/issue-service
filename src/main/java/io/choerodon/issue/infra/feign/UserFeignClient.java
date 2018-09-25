package io.choerodon.issue.infra.feign;

import io.choerodon.issue.api.dto.ProjectDTO;
import io.choerodon.issue.api.dto.UserDTO;
import io.choerodon.issue.infra.feign.dto.CreateUserWithRolesDTO;
import io.choerodon.issue.infra.feign.dto.UserSearchDTO;
import io.choerodon.issue.infra.feign.fallback.UserFeignClientFallback;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author peng.jiang
 */
@FeignClient(value = "iam-service",
        fallback = UserFeignClientFallback.class)
@Component
public interface UserFeignClient {

    /**
     * 模糊查询组织下的用户
     */
    @GetMapping(value = "/v1/organizations/{organization_id}/users/search")
    ResponseEntity<Page<UserDTO>> queryUserInOrg(@PathVariable("organization_id") Long organizationId, @RequestParam("pageRequest") PageRequest pageRequest,
                                                 @RequestBody UserSearchDTO user);

    /**
     * 根据id列表获取用户信息
     */
    @PostMapping(value = "/v1/users/ids")
    ResponseEntity<List<UserDTO>> listUsersByIds(@RequestBody Long[] ids);

    /**
     * 按照id查询项目
     */
    @RequestMapping(value = "/v1/projects/{project_id}", method = RequestMethod.GET)
    public ResponseEntity<ProjectDTO> queryProject(@PathVariable(name = "project_id") Long projectId);

    /**
     * 新增用户
     */
    @RequestMapping(value = "/v1/users/init_role", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> create(@RequestBody CreateUserWithRolesDTO userWithRoles);

    /**
     * 根据id查询用户信息
     */
    @RequestMapping(value = "/v1/users/{id}/info", method = RequestMethod.GET)
    public ResponseEntity<UserDTO> queryInfo(@PathVariable(value = "id") Long id);
}
