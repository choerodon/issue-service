package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.IssueFieldValueDTO;
import io.choerodon.issue.api.dto.SearchDTO;
import io.choerodon.issue.domain.IssueFieldValue;
import io.choerodon.mybatis.service.BaseService;

import java.util.List;

/**
 * @author shinan.chen
 * @Date 2018/9/4
 */
public interface IssueFieldValueService extends BaseService<IssueFieldValue> {
    /**
     * 根据问题类型和页面类型获取字段信息（用于创建问题和查看问题详情）
     * @param projectId
     * @param issueTypeId
     * @return
     */
    List<IssueFieldValueDTO> queryByIssueTypeIdAndPageType(Long projectId, Long issueTypeId, String pageType);

    /**
     * 填充字段对应的值
     * @param issueId
     * @param fieldValues
     */
    void supplyFieldValue(Long issueId, List<IssueFieldValueDTO> fieldValues);

    /**
     * 创建字段值
     * @param issueId
     * @param fieldValues
     */
    void createFieldValues(Long projectId, Long issueId, Long issueTypeId, List<IssueFieldValueDTO> fieldValues);

    /**
     * 更新字段值
     * @param projectId
     * @param issueId
     * @param fieldValue
     * @return
     */
    Long updateFieldValue(Long projectId, Long issueId, IssueFieldValueDTO fieldValue);

    /**
     * 查询事件单时，处理自定义字段的搜索
     * @param projectId
     * @param searchDTO
     */
    void handleFieldValueSearch(Long projectId, SearchDTO searchDTO);

    /**
     * 查询某个事件单的字段值列表，注意：此处将多选值合并到一个value中
     * @param issueId
     * @return
     */
    List<IssueFieldValue> queryByIssueId(Long issueId);
}
