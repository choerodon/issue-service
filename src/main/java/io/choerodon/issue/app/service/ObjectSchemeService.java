package io.choerodon.issue.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.issue.api.vo.ObjectSchemeVO;
import io.choerodon.issue.api.vo.ObjectSchemeSearchVO;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
public interface ObjectSchemeService {

    PageInfo<ObjectSchemeVO> pageQuery(Long organizationId, PageRequest pageRequest, ObjectSchemeSearchVO searchDTO);
}
