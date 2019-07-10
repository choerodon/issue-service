package io.choerodon.issue.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.issue.app.service.ObjectSchemeService;
import io.choerodon.issue.api.vo.ObjectSchemeSearchVO;
import io.choerodon.issue.api.vo.ObjectSchemeVO;
import io.choerodon.issue.infra.dto.ObjectScheme;
import io.choerodon.issue.infra.mapper.ObjectSchemeMapper;
import io.choerodon.issue.infra.util.PageUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/3/29
 */
@Service
public class ObjectSchemeServiceImpl implements ObjectSchemeService {
    @Autowired
    private ObjectSchemeMapper objectSchemeMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public PageInfo<ObjectSchemeVO> pageQuery(Long organizationId, PageRequest pageRequest, ObjectSchemeSearchVO searchDTO) {
        PageInfo<ObjectScheme> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(),
                PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> objectSchemeMapper.fulltextSearch(organizationId, searchDTO));
        return PageUtil.buildPageInfoWithPageInfoList(page,
                modelMapper.map(page.getList(), new TypeToken<List<ObjectSchemeVO>>() {
                }.getType()));
    }
}
