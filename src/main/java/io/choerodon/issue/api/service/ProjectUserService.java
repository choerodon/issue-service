package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.UserDTO;
import org.springframework.http.ResponseEntity;

/**
 * @author peng.jiang
 * @Date 2018/8/27
 */
public interface ProjectUserService {

    ResponseEntity<UserDTO> createUser(Long organizationId, Long projectId, UserDTO userDTO);
}
