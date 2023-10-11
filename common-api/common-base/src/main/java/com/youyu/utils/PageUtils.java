package com.youyu.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.common.PageOutput;

import java.util.List;

public class PageUtils {

    /**
     * 设置分页结果数据
     *
     * @param page
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T, K> PageOutput<K> setPageResult(Page<T> page, Class<K> clazz) {
        List<K> resultList = BeanCopyUtils.copyBeanList(page.getRecords(), clazz);
        return new PageOutput<>(resultList, page.getCurrent(), page.getPages(), page.getSize(), page.getTotal());
    }
}
