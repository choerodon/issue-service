package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.IssueRecordViewDTO;
import io.choerodon.issue.api.dto.UserDTO;
import io.choerodon.issue.api.service.AnalyzeIssueRecordService;
import io.choerodon.issue.api.service.FieldOptionService;
import io.choerodon.issue.domain.FieldOption;
import io.choerodon.issue.domain.IssueRecord;
import io.choerodon.issue.infra.enums.IssueRecordEnums;
import io.choerodon.issue.infra.feign.UserFeignClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author peng.jiang
 * @Date 2018/9/4
 */
@Component
public class AnalyzeCustomServiceImpl implements AnalyzeIssueRecordService {

    @Autowired
    FieldOptionService fieldOptionService;
    @Autowired
    private UserFeignClient iamServiceFeign;

    @Override
    public String recordType() {
        return IssueRecordEnums.FieldSource.CUSTOM.value();
    }

    @Override
    public IssueRecordViewDTO analyzeIssueRecord(Long projectId, IssueRecord issueRecord) {
        IssueRecordViewDTO viewDTO = transfBaseField(issueRecord);
        ResponseEntity<UserDTO> userDTOResponseEntity = iamServiceFeign.queryInfo(issueRecord.getCreatedBy());
        if (userDTOResponseEntity != null && userDTOResponseEntity.getBody() != null){
            viewDTO.setOperatorName(userDTOResponseEntity.getBody().getLoginName());
            viewDTO.setImageUrl(userDTOResponseEntity.getBody().getImageUrl());
        }
        String operationType = operationType(issueRecord);
        if (IssueRecordEnums.IssueRecordOperationType.UPDATE.value().equals(operationType)) {
            viewDTO.setAction(IssueRecordEnums.IssueCustomAction.UPDATE.value() + issueRecord.getFieldName());
            analyzeUpdate(viewDTO, issueRecord);
        }
        return viewDTO;
    }

    @Override
    public void analyzeUpdate(IssueRecordViewDTO viewDTO, IssueRecord issueRecord) {
        if (issueRecord.getValueType().equals(IssueRecordEnums.ValueType.OPTIONS.value())) {
            if (issueRecord.getOldValue() != null) {
                String oldValue = queryRealValue(issueRecord.getOldValue());
                viewDTO.setOldValue(oldValue);
            }
            if (issueRecord.getNewValue() != null) {
                String newValue = queryRealValue(issueRecord.getNewValue());
                viewDTO.setNewValue(newValue);
            }
        } else {
            viewDTO.setOldValue(issueRecord.getOldValue());
            viewDTO.setNewValue(issueRecord.getNewValue());
        }
    }

    /**
     * 根据id查询真实的值
     *
     * @param idString ','为间隔的id字符串
     * @return
     */
    private String queryRealValue(String idString) {
        String[] ids = StringUtils.split(idString, ",");
        StringBuilder realValue = new StringBuilder();
        for (String id : ids) {
            FieldOption fieldOption = fieldOptionService.selectByPrimaryKey(id);
            if (fieldOption != null) {
                realValue.append(fieldOption.getValue()).append(",");
            }
        }
        if (realValue.length() > 0) {
            realValue.substring(0, realValue.length() - 1);
        }
        return realValue.toString();
    }

}
