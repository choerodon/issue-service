package io.choerodon.issue.infra.mapper;


import io.choerodon.issue.domain.LookupTypeWithValues;
import io.choerodon.issue.domain.LookupValue;
import io.choerodon.mybatis.common.Mapper;

public interface LookupValueMapper extends Mapper<LookupValue> {

    LookupTypeWithValues queryLookupValueByCode(String typeCode);

}
