package io.choerodon.issue.api.service.impl;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.issue.api.dto.UserDTO;
import io.choerodon.issue.api.service.ProjectUserService;
import io.choerodon.issue.infra.feign.UserFeignClient;
import io.choerodon.issue.infra.feign.dataobject.UserDO;
import io.choerodon.issue.infra.feign.dto.CreateUserWithRolesDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author peng.jiang
 * @Date 2018/8/28
 */
@Service
public class ProjectUserServiceImpl implements ProjectUserService {

    @Autowired
    private UserFeignClient iamServiceFeign;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public ResponseEntity<UserDTO> createUser(Long organizationId, Long projectId, UserDTO userDTO) {

        UserDO userDO = modelMapper.map(userDTO, UserDO.class);
        CreateUserWithRolesDTO createUserWithRolesDTO = new CreateUserWithRolesDTO();
        createUserWithRolesDTO.setUser(userDO);
        createUserWithRolesDTO.setMemberType(ResourceLevel.USER.value());
        createUserWithRolesDTO.setSourceType(ResourceLevel.PROJECT.value());
        createUserWithRolesDTO.setSourceId(projectId);
        Set<String> roleCode = new HashSet<>();
        roleCode.add("role/project/default/administrator"); //项目角色code, todo 初始化项目角色
        createUserWithRolesDTO.setRoleCode(roleCode);
        return iamServiceFeign.create(createUserWithRolesDTO);
    }
}
