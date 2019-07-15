package io.choerodon.issue.app.service;


import io.choerodon.issue.api.vo.LookupTypeWithValuesVO;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/09/27.
 * Email: fuqianghuang01@gmail.com
 */
public interface LookupValueService {

    LookupTypeWithValuesVO queryLookupValueByCode(Long organizationId, String typeCode);
}