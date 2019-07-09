package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.LookupTypeWithValuesDTO;
import io.choerodon.issue.api.dto.LookupValueDTO;
import io.choerodon.issue.api.service.LookupValueService;
import io.choerodon.issue.domain.LookupTypeWithValues;
import io.choerodon.issue.infra.mapper.LookupValueMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public LookupTypeWithValuesDTO queryLookupValueByCode(Long organizationId, String typeCode) {
        LookupTypeWithValues typeWithValues = lookupValueMapper.queryLookupValueByCode(typeCode);
        LookupTypeWithValuesDTO result = modelMapper.map(typeWithValues, LookupTypeWithValuesDTO.class);
        result.setLookupValues(modelMapper.map(typeWithValues.getLookupValues(), new TypeToken<List<LookupValueDTO>>() {
        }.getType()));
        return result;
    }
}