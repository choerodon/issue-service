package io.choerodon.issue.infra.repository;

import io.choerodon.issue.api.vo.ObjectSchemeFieldSearchVO;
import io.choerodon.issue.infra.dto.ObjectSchemeFieldDTO;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface ObjectSchemeFieldRepository {
    ObjectSchemeFieldDTO create(ObjectSchemeFieldDTO field);

    void delete(Long fieldId);

    void update(ObjectSchemeFieldDTO field);

    ObjectSchemeFieldDTO queryById(Long organizationId, Long projectId, Long fieldId);

    List<ObjectSchemeFieldDTO> listQuery(Long organizationId, Long projectId, ObjectSchemeFieldSearchVO searchDTO);

    ObjectSchemeFieldDTO queryByFieldCode(Long organizationId, Long projectId, String fieldCode);
}
