package io.choerodon.issue.infra.mapper;

import io.choerodon.issue.domain.Attachment;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2018/9/3
 */

@Component
public interface AttachmentMapper extends BaseMapper<Attachment> {
    List<Attachment> fulltextSearch(@Param("attachment") Attachment attachment, @Param("param") String param);

    List<Attachment> queryByIssue(@Param("attachment") Attachment attachment, @Param("issueId") Long issueId);
}
