package io.choerodon.issue.infra.feign.fallback;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.OrganizationDTO;
import io.choerodon.issue.api.dto.ProjectDTO;
import io.choerodon.issue.api.dto.UserDTO;
import io.choerodon.issue.infra.feign.UserFeignClient;
import io.choerodon.issue.infra.feign.dto.CreateUserWithRolesDTO;
import io.choerodon.issue.infra.feign.dto.UserSearchDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public class UserFeignClientFallback implements UserFeignClient {


    @Override
    public ResponseEntity<Page<UserDTO>> queryUserInOrg(Long organizationId, PageRequest pageRequest, UserSearchDTO user) {
        throw new CommonException("error.userFeign.queryUserInOrg");
    }

    @Override
    public ResponseEntity<ProjectDTO> queryProject(Long projectId) {
        throw new CommonException("error.userFeign.queryProject");
    }

    @Override
    public ResponseEntity<List<UserDTO>> listUsersByIds(Long[] ids) {
        throw new CommonException("error.userFeign.listUsersByIds");
    }

    @Override
    public ResponseEntity<UserDTO> create(CreateUserWithRolesDTO userWithRoles) {
        throw new CommonException("error.iamServiceFeignFallback.create");
    }

    @Override
    public ResponseEntity<UserDTO> queryInfo(Long id) {
        throw new CommonException("error.iamServiceFeignFallback.queryInfo");
    }

    @Override
    public ResponseEntity<Page<ProjectDTO>> queryProjectsByOrgId(Long organizationId, Integer page, Integer size, String[] sort, String name, String code, Boolean enabled, String[] params) {
        throw new CommonException("error.iamServiceFeignFallback.queryProjectsByOrgId");
    }
}


