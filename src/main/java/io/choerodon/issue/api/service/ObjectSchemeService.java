package io.choerodon.issue.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.issue.api.dto.ObjectSchemeDTO;
import io.choerodon.issue.api.dto.ObjectSchemeSearchDTO;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
public interface ObjectSchemeService {

    PageInfo<ObjectSchemeDTO> pageQuery(Long organizationId, PageRequest pageRequest, ObjectSchemeSearchDTO searchDTO);
}
