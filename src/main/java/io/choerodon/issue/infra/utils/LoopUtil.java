package io.choerodon.issue.infra.utils;

import io.choerodon.issue.api.dto.FieldOptionDTO;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @date 2018/8/28
 */
public class LoopUtil {
    /**
     * 查询字段选项值子集
     *
     * @param parentId
     * @param map
     * @return
     */
    public static List<FieldOptionDTO> queryFieldOptionChild(Long parentId, Map<Long, List<FieldOptionDTO>> map) {
        List<FieldOptionDTO> parents = map.get(parentId);
        if (parents != null) {
            for (FieldOptionDTO parent : parents) {
                List<FieldOptionDTO> child = queryFieldOptionChild(parent.getId(), map);
                if (child != null) {
                    parent.setChildren(child);
                }
            }
            parents = parents.stream().sorted(Comparator.comparing(FieldOptionDTO::getSequence)).collect(Collectors.toList());
        }
        return parents;
    }
}
