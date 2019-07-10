package io.choerodon.issue.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.FieldDataLogCreateDTO;
import io.choerodon.issue.api.dto.FieldDataLogDTO;
import io.choerodon.issue.api.service.FieldDataLogService;
import io.choerodon.issue.domain.FieldDataLog;
import io.choerodon.issue.infra.enums.ObjectSchemeCode;
import io.choerodon.issue.infra.mapper.FieldDataLogMapper;
import io.choerodon.issue.infra.repository.FieldDataLogRepository;
import io.choerodon.issue.infra.utils.EnumUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/6/19
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class FieldDataLogServiceImpl implements FieldDataLogService {
    @Autowired
    private FieldDataLogMapper fieldDataLogMapper;
    @Autowired
    private FieldDataLogRepository fieldDataLogRepository;

    private static final String ERROR_PAGECODE_ILLEGAL = "error.pageCode.illegal";
    private static final String ERROR_CONTEXT_ILLEGAL = "error.context.illegal";
    private static final String ERROR_SCHEMECODE_ILLEGAL = "error.schemeCode.illegal";
    private static final String ERROR_OPTION_ILLEGAL = "error.option.illegal";
    private static final String ERROR_FIELDTYPE_ILLEGAL = "error.fieldType.illegal";
    private static final String ERROR_SYSTEM_ILLEGAL = "error.system.illegal";

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public FieldDataLogDTO createDataLog(Long projectId, String schemeCode, FieldDataLogCreateDTO create) {
        FieldDataLog dataLog = modelMapper.map(create, FieldDataLog.class);
        dataLog.setProjectId(projectId);
        dataLog.setSchemeCode(schemeCode);
        return modelMapper.map(fieldDataLogRepository.create(dataLog), FieldDataLogDTO.class);
    }

    @Override
    public void deleteByFieldId(Long projectId, Long fieldId) {
        FieldDataLog delete = new FieldDataLog();
        delete.setFieldId(fieldId);
        delete.setProjectId(projectId);
        fieldDataLogMapper.delete(delete);
    }

    @Override
    public List<FieldDataLogDTO> queryByInstanceId(Long projectId, Long instanceId, String schemeCode) {
        if (!EnumUtil.contain(ObjectSchemeCode.class, schemeCode)) {
            throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
        }
        return modelMapper.map(fieldDataLogMapper.queryByInstanceId(projectId, schemeCode, instanceId), new TypeToken<List<FieldDataLogDTO>>() {
        }.getType());
    }
}
