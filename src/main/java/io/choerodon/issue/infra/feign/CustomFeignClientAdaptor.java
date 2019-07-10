package io.choerodon.issue.infra.feign;

import feign.RequestLine;
import io.choerodon.issue.api.vo.ExecuteResult;
import io.choerodon.issue.api.vo.InputVO;
import io.choerodon.issue.infra.config.FeignConfiguration;
import io.choerodon.issue.api.vo.payload.TransformInfo;
import io.choerodon.issue.infra.feign.fallback.CustomFeignClientAdaptorFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/9/17
 */
@FeignClient(name = "customFeignClient", fallback = CustomFeignClientAdaptorFallBack.class, configuration = FeignConfiguration.class)
public interface CustomFeignClientAdaptor {

    @RequestLine("GET")
    void action(URI baseUri);

    /**
     * 调用对应服务，通过条件验证过滤掉转换
     *
     * @param baseUri
     * @param transforms
     * @return
     */
    @RequestLine("POST")
    ResponseEntity<List<TransformInfo>> filterTransformsByConfig(URI baseUri, List<TransformInfo> transforms);

    /**
     * 调用对应服务，执行条件，验证，后置处理
     *
     * @param baseUri
     * @param inputVO
     * @return
     */
    @RequestLine("POST")
    ResponseEntity<ExecuteResult> executeConfig(URI baseUri, InputVO inputVO);
}
