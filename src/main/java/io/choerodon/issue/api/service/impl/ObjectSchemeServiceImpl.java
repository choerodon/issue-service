package io.choerodon.issue.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.issue.api.dto.ObjectSchemeDTO;
import io.choerodon.issue.api.dto.ObjectSchemeSearchDTO;
import io.choerodon.issue.api.service.ObjectSchemeService;
import io.choerodon.issue.domain.ObjectScheme;
import io.choerodon.issue.infra.mapper.ObjectSchemeMapper;
import io.choerodon.issue.infra.utils.PageUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
@Component
public class ObjectSchemeServiceImpl implements ObjectSchemeService {
    @Autowired
    private ObjectSchemeMapper objectSchemeMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public PageInfo<ObjectSchemeDTO> pageQuery(Long organizationId, PageRequest pageRequest, ObjectSchemeSearchDTO searchDTO) {
        PageInfo<ObjectScheme> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> objectSchemeMapper.fulltextSearch(organizationId, searchDTO));
        return PageUtil.buildPageInfoWithPageInfoList(page,
                modelMapper.map(page.getList(), new TypeToken<List<ObjectSchemeDTO>>() {
                }.getType()));
    }
}
