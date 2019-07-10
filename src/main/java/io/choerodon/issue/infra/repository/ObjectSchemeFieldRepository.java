package io.choerodon.issue.infra.repository;

import io.choerodon.issue.api.vo.ObjectSchemeFieldSearchVO;
import io.choerodon.issue.infra.dto.ObjectSchemeField;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface ObjectSchemeFieldRepository {
    ObjectSchemeField create(ObjectSchemeField field);

    void delete(Long fieldId);

    void update(ObjectSchemeField field);

    ObjectSchemeField queryById(Long organizationId, Long projectId, Long fieldId);

    List<ObjectSchemeField> listQuery(Long organizationId, Long projectId, ObjectSchemeFieldSearchVO searchDTO);

    ObjectSchemeField queryByFieldCode(Long organizationId, Long projectId, String fieldCode);
}
