package io.choerodon.issue.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.app.service.FieldDataLogService;
import io.choerodon.issue.api.vo.FieldDataLogCreateVO;
import io.choerodon.issue.api.vo.FieldDataLogVO;
import io.choerodon.issue.infra.dto.FieldDataLog;
import io.choerodon.issue.infra.enums.ObjectSchemeCode;
import io.choerodon.issue.infra.mapper.FieldDataLogMapper;
import io.choerodon.issue.infra.repository.FieldDataLogRepository;
import io.choerodon.issue.infra.util.EnumUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/6/19
 */
@Service
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
    public FieldDataLogVO createDataLog(Long projectId, String schemeCode, FieldDataLogCreateVO create) {
        FieldDataLog dataLog = modelMapper.map(create, FieldDataLog.class);
        dataLog.setProjectId(projectId);
        dataLog.setSchemeCode(schemeCode);
        return modelMapper.map(fieldDataLogRepository.create(dataLog), FieldDataLogVO.class);
    }

    @Override
    public void deleteByFieldId(Long projectId, Long fieldId) {
        FieldDataLog delete = new FieldDataLog();
        delete.setFieldId(fieldId);
        delete.setProjectId(projectId);
        fieldDataLogMapper.delete(delete);
    }

    @Override
    public List<FieldDataLogVO> queryByInstanceId(Long projectId, Long instanceId, String schemeCode) {
        if (!EnumUtil.contain(ObjectSchemeCode.class, schemeCode)) {
            throw new CommonException(ERROR_SCHEMECODE_ILLEGAL);
        }
        return modelMapper.map(fieldDataLogMapper.queryByInstanceId(projectId, schemeCode, instanceId), new TypeToken<List<FieldDataLogVO>>() {
        }.getType());
    }
}
