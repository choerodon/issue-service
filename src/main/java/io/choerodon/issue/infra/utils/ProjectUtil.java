package io.choerodon.issue.infra.utils;

import io.choerodon.issue.api.dto.ProjectDTO;
import io.choerodon.issue.infra.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/9/7
 * 通过projectId获取organizationId
 */
@Component
public class ProjectUtil {
    @Autowired
    private UserFeignClient iamServiceFeign;

    public static final Map<Long, ProjectDTO> map = new HashMap<>();

    public Long getOrganizationId(Long projectId) {
        ProjectDTO projectDTO = map.get(projectId);
        if (projectDTO != null) {
            return projectDTO.getOrganizationId();
        } else {
            projectDTO = iamServiceFeign.queryProject(projectId).getBody();
            if (projectDTO != null) {
                map.put(projectId, projectDTO);
                return projectDTO.getOrganizationId();
            }
        }
        return null;
    }
}
