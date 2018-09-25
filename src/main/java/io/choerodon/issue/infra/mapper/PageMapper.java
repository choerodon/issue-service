package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shinan.chen
 * @date 2018/8/22
 */
@Component
public interface PageMapper extends BaseMapper<Page> {
    List<Page> fulltextSearch(@Param("page") Page page, @Param("param") String param);
}
