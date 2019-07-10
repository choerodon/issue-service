package io.choerodon.issue.app.service.impl;

import io.choerodon.issue.app.service.LookupValueService;
import io.choerodon.issue.api.vo.LookupTypeWithValuesVO;
import io.choerodon.issue.api.vo.LookupValueVO;
import io.choerodon.issue.infra.dto.LookupTypeWithValuesDTO;
import io.choerodon.issue.infra.mapper.LookupValueMapper;
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
public class LookupValueServiceImpl implements LookupValueService {

    @Autowired
    private LookupValueMapper lookupValueMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public LookupTypeWithValuesVO queryLookupValueByCode(Long organizationId, String typeCode) {
        LookupTypeWithValuesDTO typeWithValues = lookupValueMapper.queryLookupValueByCode(typeCode);
        LookupTypeWithValuesVO result = modelMapper.map(typeWithValues, LookupTypeWithValuesVO.class);
        result.setLookupValues(modelMapper.map(typeWithValues.getLookupValues(), new TypeToken<List<LookupValueVO>>() {
        }.getType()));
        return result;
    }
}