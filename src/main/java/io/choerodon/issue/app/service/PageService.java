package io.choerodon.issue.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.issue.api.vo.PageVO;
import io.choerodon.issue.api.vo.PageSearchVO;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface PageService {
    PageInfo<PageVO> pageQuery(Long organizationId, PageRequest pageRequest, PageSearchVO searchDTO);
}
