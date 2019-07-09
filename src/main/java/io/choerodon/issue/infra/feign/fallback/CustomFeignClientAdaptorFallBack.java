package io.choerodon.issue.infra.feign.fallback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.ExecuteResult;
import io.choerodon.issue.api.dto.InputDTO;
import io.choerodon.issue.api.dto.payload.TransformInfo;
import io.choerodon.issue.infra.feign.CustomFeignClientAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/9/20
 */
public class CustomFeignClientAdaptorFallBack implements CustomFeignClientAdaptor {

    private static final Logger logger = LoggerFactory.getLogger(CustomFeignClientAdaptorFallBack.class);

    @Override
    public void action(URI baseUri) {
        logger.info("action");
    }

    @Override
    public ResponseEntity<List<TransformInfo>> filterTransformsByConfig(URI baseUri, List<TransformInfo> transforms) {
        throw new CommonException("error.customFeignClientAdaptor.filterTransformsByConfig");
    }

    @Override
    public ResponseEntity<ExecuteResult> executeConfig(URI baseUri, InputDTO inputDTO) {
        throw new CommonException("error.customFeignClientAdaptor.filterTransformsByConfig");
    }
}
