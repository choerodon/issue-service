package io.choerodon.issue.infra.mapper;


import io.choerodon.issue.infra.dto.LookupTypeWithValuesDTO;
import io.choerodon.issue.infra.dto.LookupValueDTO;
import io.choerodon.mybatis.common.Mapper;

public interface LookupValueMapper extends Mapper<LookupValueDTO> {
    LookupTypeWithValuesDTO queryLookupValueByCode(String typeCode);
}
