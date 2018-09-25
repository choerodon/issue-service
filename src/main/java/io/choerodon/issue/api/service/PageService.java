package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.FieldDTO;
import io.choerodon.issue.api.dto.PageDTO;
import io.choerodon.issue.api.dto.PageDetailDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/22
 */
public interface PageService extends BaseService<io.choerodon.issue.domain.Page> {

    PageDetailDTO queryById(Long organizationId, Long pageId);

    PageDetailDTO create(Long organizationId, PageDetailDTO pageDetailDTO);

    PageDetailDTO update(Long organizationId, PageDetailDTO pageDetailDTO);

    Boolean delete(Long organizationId, Long pageId);

    Map<String, Object> checkDelete(Long organizationId, Long pageId);

    io.choerodon.core.domain.Page<PageDTO> pageQuery(Long organizationId, PageRequest pageRequest, PageDTO pageDTO, String param);

    List<PageDTO> listQuery(PageDTO pageDTO, String param);

    Boolean checkName(Long organizationId, String name, Long id);

    void createFields(Long organizationId, Long pageId, List<FieldDTO> fieldDTOs);
}
