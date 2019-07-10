package io.choerodon.issue.app.service.impl;


import io.choerodon.issue.app.service.LookupTypeService;
import io.choerodon.issue.api.vo.LookupTypeVO;
import io.choerodon.issue.infra.mapper.LookupTypeMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/09/27.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class LookupTypeServiceImpl implements LookupTypeService {

    @Autowired
    private LookupTypeMapper lookupTypeMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<LookupTypeVO> listLookupType(Long organizationId) {
        return modelMapper.map(lookupTypeMapper.selectAll(), new TypeToken<List<LookupTypeVO>>() {
        }.getType());
    }

}
