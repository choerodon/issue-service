package io.choerodon.issue.api.service;

import io.choerodon.issue.api.dto.*;
import io.choerodon.issue.domain.ObjectSchemeField;
import io.choerodon.issue.domain.PageField;

import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2019/4/1
 */
public interface PageFieldService {

    /**
     * 根据pageCode和context获取pageField，不存在则创建
     *
     * @param organizationId
     * @param projectId
     * @param pageCode
     * @param context
     * @return
     */
    List<PageField> queryPageField(Long organizationId, Long projectId, String pageCode, String context);

    /**
     * 组织层/项目层 根据页面编码获取字段列表
     *
     * @param organizationId
     * @param projectId
     * @param pageCode
     * @param context
     * @return
     */
    Map<String, Object> listQuery(Long organizationId, Long projectId, String pageCode, String context);

    /**
     * 组织层/项目层 调整字段顺序
     *
     * @param organizationId
     * @param projectId
     * @param adjustOrder
     */
    PageFieldDTO adjustFieldOrder(Long organizationId, Long projectId, String pageCode, AdjustOrderDTO adjustOrder);

    /**
     * 组织层/项目层 更新页面字段
     *
     * @param organizationId
     * @param projectId
     * @param fieldId
     * @param updateDTO
     * @return
     */
    PageFieldDTO update(Long organizationId, Long projectId, String pageCode, Long fieldId, PageFieldUpdateDTO updateDTO);

    /**
     * 组织层初始化页面字段
     *
     * @param organizationId
     */
    void initPageFieldByOrg(Long organizationId);

    /**
     * 组织层 创建页面字段
     *
     * @param organizationId
     * @param field
     */
    void createByFieldWithOrg(Long organizationId, ObjectSchemeField field);

    /**
     * 项目层 创建页面字段
     *
     * @param organizationId
     * @param projectId
     * @param field
     */
    void createByFieldWithPro(Long organizationId, Long projectId, ObjectSchemeField field);

    /**
     * 删除字段
     *
     * @param fieldId
     */
    void deleteByFieldId(Long fieldId);

    /**
     * 界面上获取字段列表，带有字段选项
     *
     * @param organizationId
     * @param projectId
     * @param paramDTO
     * @return
     */
    List<PageFieldViewDTO> queryPageFieldViewList(Long organizationId, Long projectId, PageFieldViewParamDTO paramDTO);

    /**
     * 根据实例id从界面上获取字段列表，带有字段值、字段选项
     *
     * @param organizationId
     * @param projectId
     * @param instanceId
     * @param paramDTO
     * @return
     */
    List<PageFieldViewDTO> queryPageFieldViewListWithInstanceId(Long organizationId, Long projectId, Long instanceId, PageFieldViewParamDTO paramDTO);

    /**
     * 根据实例ids获取全部自定义字段的CodeValue键值对
     *
     * @param organizationId
     * @param projectId
     * @param instanceIds
     * @return
     */
    Map<Long, Map<String, String>> queryFieldValueWithIssueIdsForAgileExport(Long organizationId, Long projectId, List<Long> instanceIds);
}
