package io.choerodon.issue.infra.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author shinan.chen
 * @date 2018/8/13
 */
public class ListChangeUtil {
    private ListChangeUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 计算列表newList相对于oldList的增加的情况，兼容任何类型元素的列表数据结构
     *
     * @param newList 本列表
     * @param oldList 对照列表
     * @return 返回增加的元素组成的列表
     */
    public static <E> List<E> getAddList(List<E> newList, List<E> oldList, BiPredicate myEquals) {
        List<E> addList = new ArrayList<>();
        if (newList != null) {
            for (int i = 0; i < newList.size(); i++) {
                if (!myListContains(oldList, newList.get(i), myEquals)) {
                    addList.add(newList.get(i));
                }
            }
        }
        return addList;
    }

    /**
     * 计算列表newList相对于oldList的减少的情况，兼容任何类型元素的列表数据结构
     *
     * @param newList 本列表
     * @param oldList 对照列表
     * @return 返回减少的元素组成的列表
     */
    public static <E> List<E> getReduceList(List<E> newList, List<E> oldList, BiPredicate myEquals) {
        List<E> reduceList = new ArrayList<>();
        if (oldList != null) {
            for (int i = 0; i < oldList.size(); i++) {
                if (!myListContains(newList, oldList.get(i), myEquals)) {
                    reduceList.add(oldList.get(i));
                }
            }
        }
        return reduceList;
    }

    /**
     * 判断元素element是否是sourceList列表中的一个子元素
     *
     * @param sourceList 源列表
     * @param element    待判断的包含元素
     * @return 包含返回 true，不包含返回 false
     * @Param myEquals   自定义相等方法
     */
    private static <E> boolean myListContains(List<E> sourceList, E element, BiPredicate myEquals) {
        if (sourceList == null || element == null) {
            return false;
        }
        if (sourceList.isEmpty()) {
            return false;
        }
        for (E tip : sourceList) {
            if (myEquals.test(tip, element)) {
                return true;
            }
        }
        return false;
    }
}
