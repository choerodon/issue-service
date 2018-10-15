package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.IssueRecordViewDTO;
import io.choerodon.issue.api.dto.UserDTO;
import io.choerodon.issue.api.service.AnalyzeIssueRecordService;
import io.choerodon.issue.api.service.IssueRecordService;
import io.choerodon.issue.domain.Issue;
import io.choerodon.issue.domain.IssueRecord;
import io.choerodon.issue.infra.enums.IssueRecordEnums;
import io.choerodon.issue.infra.feign.UserFeignClient;
import io.choerodon.issue.infra.mapper.IssueMapper;
import io.choerodon.issue.infra.mapper.IssueRecordMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author peng.jiang
 * @Date 2018/9/4
 */
@Component
@RefreshScope
public class IssueRecordServiceImpl extends BaseServiceImpl<IssueRecord> implements IssueRecordService {

    @Autowired
    private IssueRecordMapper issueRecordMapper;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private UserFeignClient iamServiceFeign;

    @Autowired
    private AnalyzeServiceManager analyzeServiceManager;

    @Override
    public List<IssueRecordViewDTO> queryByIssueId(Long projectId, Long issueId) {
        IssueRecord issueRecord = new IssueRecord();
        issueRecord.setIssueId(issueId);
        List<IssueRecord> issueRecords = issueRecordPack(issueRecordMapper.select(issueRecord));
        List<IssueRecordViewDTO> viewDTOS = new ArrayList<>();
        // 创建事件单记录
        Issue issue = issueMapper.selectByPrimaryKey(issueId);
        IssueRecordViewDTO creatIssueRecord = new IssueRecordViewDTO();
        ResponseEntity<UserDTO> userDTOResponseEntity = iamServiceFeign.queryInfo(issue.getCreatedBy());
        if (userDTOResponseEntity != null && userDTOResponseEntity.getBody() != null){
            creatIssueRecord.setOperatorName(userDTOResponseEntity.getBody().getLoginName());
            creatIssueRecord.setImageUrl(userDTOResponseEntity.getBody().getImageUrl());
        }
        creatIssueRecord.setAction(IssueRecordEnums.IssueSystemAction.CREATE_ISSUE.value());
        viewDTOS.add(creatIssueRecord);
        for (IssueRecord temp:issueRecords) {
            IssueRecordViewDTO viewDTO = analyzeIssueRecord(projectId, temp);
            viewDTOS.add(viewDTO);
        }
        return viewDTOS;
    }

    @Override
    @Transactional
    public List<IssueRecord> create(Long projectId, Long issueId, List<IssueRecord> issueRecords) {
        if (issueRecords == null || issueRecords.isEmpty()){
            throw new CommonException("error.issueRecord.issueRecords.isEmpty");
        }
        IssueRecord issueRecord = issueRecords.get(0);
        issueRecord.setIssueId(issueId);
        int isInsert = issueRecordMapper.insert(issueRecord);
        if (isInsert != 1){
            throw new CommonException("error.issueRecord.issueRecords.insert");
        }
        issueRecord = issueRecordMapper.selectByPrimaryKey(issueRecord.getId());
        issueRecord.setGroupId(issueRecord.getId());
        int isUpdate = issueRecordMapper.updateByPrimaryKey(issueRecord);
        if (isUpdate != 1){
            throw new CommonException("error.issueRecord.issueRecords.insert");
        }
        //一次操作中会产生多个操作记录时，设置groupId为第一条数据的id
        if (issueRecords.size() > 1){
            issueRecords.remove(0);
            for (IssueRecord record : issueRecords) {
                record.setGroupId(issueRecord.getId());
                record.setIssueId(issueId);
            }
            int insertList = issueRecordMapper.insertList(issueRecords);
            if (insertList != issueRecords.size()){
                throw new CommonException("error.issueRecord.issueRecords.insert");
            }
        }
        IssueRecord serach = new IssueRecord();
        serach.setGroupId(issueRecord.getGroupId());
        return issueRecordPack(issueRecordMapper.select(serach));
    }

    @Override
    @Transactional
    public List<IssueRecord> create(Long projectId, Long issueId, IssueRecord issueRecord) {
        return create(projectId, issueId, Arrays.asList(issueRecord));
    }

    /**
     * 解析修改记录
     * @param issueRecord
     * @return
     */
    private IssueRecordViewDTO analyzeIssueRecord(Long projectId, IssueRecord issueRecord) {
        List<AnalyzeIssueRecordService> analyzeServices = analyzeServiceManager.getAnalyzeServices();
        for(AnalyzeIssueRecordService service: analyzeServices){
            if(service.matchRecordType(issueRecord.getFieldSource())){
                return service.analyzeIssueRecord(projectId, issueRecord);
            }
        }
        return null;
    }

    /**
     * 数据封装，将附件这种有多条数据的，放入一个组中
     * @param issueRecords
     * @return
     */
    private List<IssueRecord> issueRecordPack (List<IssueRecord> issueRecords) {
        if (issueRecords == null || issueRecords.isEmpty()) {
            return Collections.emptyList();
        }
        List<IssueRecord> removeList = new ArrayList<>();
        for (IssueRecord issueRecord:issueRecords) {
            //文件类型，遍历链表之前的数据,将这条数据放到他的组中
            if (IssueRecordEnums.FieldSource.ATTACHMENT.value().equals(issueRecord.getFieldSource()) && issueRecord.getGroupId() != null) {
                for (IssueRecord issueRecord2:issueRecords) {
                    if (IssueRecordEnums.FieldSource.ATTACHMENT.value().equals(issueRecord2.getFieldSource()) && issueRecord2.getGroupId() != null
                       && issueRecord2.getGroupId().equals(issueRecord.getId()) && issueRecord2.getId() != issueRecord.getId()) {
                        List<IssueRecord> list = issueRecord.getIssueRecords();
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.add(issueRecord2);
                        issueRecord.setIssueRecords(list);
                        removeList.add(issueRecord2);
                    }
                }
            }
        }
        issueRecords.removeAll(removeList);
        return issueRecords;
    }
}
