package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.IssueRecordViewDTO;
import io.choerodon.issue.domain.IssueRecord;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;

/**
 * @author peng.jiang
 * @Date 2018/9/4
 */
public interface IssueRecordService extends BaseService<IssueRecord> {

    /**
     * 查询事件单下的记录
     * @param projectId 项目id
     * @param issueId 问题id
     * @return 记录列表
     */
    List<IssueRecordViewDTO> queryByIssueId(Long projectId, Long issueId);

    /**
     * 创建记录
     * @param projectId 项目id
     * @param issueId 问题id
     * @param issueRecords 记录列表
     * @return
     */
    List<IssueRecord> create(Long projectId, Long issueId, List<IssueRecord> issueRecords);

    /**
     * 创建单个记录,对上一个方法的重写
     * @param projectId 项目id
     * @param issueId 问题id
     * @param issueRecord 记录
     *
     *                   fieldSource     fieldName
     *       事件单         system          字段name
     * 事件单扩展字段         custom
     *         评论         reply
     *         附件       attachment
     *
     * oldId   oldValue    newId   newValue
     * null     null        1         xx/null        =>  创建,根据newId 查询数据
     * 1        xx          1         xx/null        =>  修改
     * 1        xx          null      null           =>  删除
     *
     * @return
     */
    List<IssueRecord> create(Long projectId, Long issueId, IssueRecord issueRecord);
}
