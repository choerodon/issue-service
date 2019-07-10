package io.choerodon.issue.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.issue.api.dto.PageDTO;
import io.choerodon.issue.api.dto.PageSearchDTO;
import io.choerodon.issue.api.service.PageService;
import io.choerodon.issue.domain.Page;
import io.choerodon.issue.infra.mapper.PageMapper;
import io.choerodon.issue.infra.utils.PageUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
@Component
public class PageServiceImpl implements PageService {
    @Autowired
    private PageMapper pageMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public PageInfo<PageDTO> pageQuery(Long organizationId, PageRequest pageRequest, PageSearchDTO searchDTO) {
        PageInfo<Page> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> pageMapper.fulltextSearch(organizationId, searchDTO));
        return PageUtil.buildPageInfoWithPageInfoList(page,
                modelMapper.map(page.getList(), new TypeToken<List<PageDTO>>() {
                }.getType()));
    }
}
