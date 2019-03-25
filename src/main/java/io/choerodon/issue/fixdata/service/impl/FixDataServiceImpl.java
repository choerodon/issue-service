package io.choerodon.issue.fixdata.service.impl;

import io.choerodon.issue.fixdata.service.FixDataService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author shinan.chen
 * @date 2018/10/25
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class FixDataServiceImpl implements FixDataService {
}
