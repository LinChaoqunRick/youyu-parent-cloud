package utils;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.page.PageOutput;

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
        List<K> resultList = BeanUtil.copyToList(page.getRecords(), clazz);
        return new PageOutput<>(resultList, page.getCurrent(), page.getPages(), page.getSize(), page.getTotal());
    }
}
