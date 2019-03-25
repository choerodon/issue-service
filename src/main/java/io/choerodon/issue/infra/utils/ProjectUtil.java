package io.choerodon.issue.infra.utils;

import io.choerodon.core.exception.CommonException;
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

    protected static final Map<Long, ProjectDTO> map = new HashMap<>();

    public Long getOrganizationId(Long projectId) {
        return queryProject(projectId).getOrganizationId();
    }

    public String getCode(Long projectId) {
        return queryProject(projectId).getCode();
    }

    public String getName(Long projectId) {
        return queryProject(projectId).getName();
    }

    private ProjectDTO queryProject(Long projectId) {
        ProjectDTO projectDTO = map.get(projectId);
        if (projectDTO != null) {
            return projectDTO;
        } else {
            projectDTO = iamServiceFeign.queryProject(projectId).getBody();
            if (projectDTO != null) {
                map.put(projectId, projectDTO);
                return projectDTO;
            } else {
                throw new CommonException("error.queryProject.notFound");
            }
        }
    }
}
