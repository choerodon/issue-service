package io.choerodon.issue.api.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shinan.chen
 * @date 2018/9/11
 */
public class SearchDTO {
    /**
     * 查询参数
     */
    private Map<String, Object> searchArgs;

    /**
     * 查询参数转换参数
     */
    private Map<String, Object> searchArgsIds;

    /**
     * 全量搜索
     */
    private String param;

    /**
     * 全量搜索转换参数
     */
    private Map<String, Object> paramIds;

    /**
     * 根据自定义字段查询
     */
    private Map<Long, String> customFieldSearchArgs;//字段id - 字段值

    public Map<String, Object> getSearchArgs() {
        return searchArgs;
    }

    public void setSearchArgs(Map<String, Object> searchArgs) {
        this.searchArgs = searchArgs;
    }

    public Map<Long, String> getCustomFieldSearchArgs() {
        return customFieldSearchArgs;
    }

    public void setCustomFieldSearchArgs(Map<Long, String> customFieldSearchArgs) {
        this.customFieldSearchArgs = customFieldSearchArgs;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Map<String, Object> getSearchArgsIds() {
        searchArgsIds = this.searchArgsIds == null ? new HashMap<>() : this.searchArgsIds;
        return this.searchArgsIds;
    }

    public void setSearchArgsIds(Map<String, Object> searchArgsIds) {
        this.searchArgsIds = searchArgsIds;
    }

    public Map<String, Object> getParamIds() {
        paramIds = this.paramIds == null ? new HashMap<>() : this.paramIds;
        return this.paramIds;
    }

    public void setParamIds(Map<String, Object> paramIds) {
        this.paramIds = paramIds;
    }
}
