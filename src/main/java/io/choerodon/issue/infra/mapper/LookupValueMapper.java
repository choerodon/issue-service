package io.choerodon.issue.infra.mapper;


import io.choerodon.issue.infra.dto.LookupTypeWithValues;
import io.choerodon.issue.infra.dto.LookupValue;
import io.choerodon.mybatis.common.Mapper;

public interface LookupValueMapper extends Mapper<LookupValue> {
    LookupTypeWithValues queryLookupValueByCode(String typeCode);
}
