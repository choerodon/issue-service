package io.choerodon.issue.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.issue.api.dto.PageDTO;
import io.choerodon.issue.api.dto.PageSearchDTO;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface PageService {
    PageInfo<PageDTO> pageQuery(Long organizationId, PageRequest pageRequest, PageSearchDTO searchDTO);
}
