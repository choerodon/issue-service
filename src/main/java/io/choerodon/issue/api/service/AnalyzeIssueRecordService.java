package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.IssueRecordViewDTO;
import io.choerodon.issue.domain.IssueRecord;
import io.choerodon.issue.infra.enums.IssueRecordEnums;
import io.choerodon.core.exception.CommonException;

/**
 * @author peng.jiang
 * @Date 2018/9/4
 */
public interface AnalyzeIssueRecordService {

    /**
     * 当前实现类处理的类型.
     *
     * @return
     */
    String recordType();

    /**
     * 判断记录类型（system,custom,reply,attachment）是否匹配.
     *
     * @param recordType 记录类型
     * @return
     */
    default boolean matchRecordType(String recordType) {
        return recordType().equalsIgnoreCase(recordType);
    }

    /**
     * 判断修改记录是在做新增,修改,删除操作
     *
     * @param recordType 记录类型
     * @return
     */
    default String operationType(IssueRecord issueRecord) {
        Long oldId = issueRecord.getOldId();
        String oldValue = issueRecord.getOldValue();
        Long newId = issueRecord.getNewId();
        String newValue = issueRecord.getNewValue();
        if ((oldId == null && oldValue == null) && (newId != null || newValue != null)) {
            return IssueRecordEnums.IssueRecordOperationType.CREATE.value();
        } else if ((oldId != null || oldValue != null) && (newId != null || newValue != null)) {
            return IssueRecordEnums.IssueRecordOperationType.UPDATE.value();
        } else if ((oldId != null || oldValue != null)) {    //&& (newId == null && newValue == null)
            return IssueRecordEnums.IssueRecordOperationType.DELETE.value();
        } else {
            throw new CommonException("error.issueRecord.analyze");
        }
    }

    /**
     * 判断记录类型（system,custom,reply,attachment）是否匹配.
     *
     * @param recordType 记录类型
     * @return
     */
    default IssueRecordViewDTO transfBaseField(IssueRecord issueRecord) {
        IssueRecordViewDTO viewDTO = new IssueRecordViewDTO();
        viewDTO.setId(issueRecord.getId());
        viewDTO.setFieldSource(issueRecord.getFieldSource());
        viewDTO.setCreationDate(issueRecord.getCreationDate());
        return viewDTO;
    }

    /**
     * 解析修改记录
     *
     * @param issueRecord
     * @return
     */
    public IssueRecordViewDTO analyzeIssueRecord(Long projectId, IssueRecord issueRecord);

    /**
     * 创建记录解析
     *
     * @param viewDTO
     * @return
     */
    public default void analyzeCreate(IssueRecordViewDTO viewDTO, IssueRecord issueRecord) {
    }

    /**
     * 修改记录解析
     *
     * @param viewDTO
     * @return
     */
    public default void analyzeUpdate(IssueRecordViewDTO viewDTO, IssueRecord issueRecord) {
    }

    /**
     * 删除记录解析
     *
     * @param viewDTO
     * @return
     */
    public default void analyzeDelete(IssueRecordViewDTO viewDTO, IssueRecord issueRecord) {
    }
}
