package com.youyu.dto.moment;

import com.youyu.dto.page.PageBase;
import lombok.Data;

import java.util.Set;

@Data
public class MomentListInput extends PageBase {
    private String userIds;
    private String orderBy = "create_time";

    // 允许排序的字段白名单
    private static final Set<String> ALLOWED_ORDER_FIELDS = Set.of(
        "create_time", "id", "update_time", "support_count"
    );

    /**
     * 验证 orderBy 字段是否合法
     * @return 合法的 orderBy 字段，如果不合法则返回默认值
     */
    public String getValidatedOrderBy() {
        if (orderBy == null || !ALLOWED_ORDER_FIELDS.contains(orderBy)) {
            return "create_time";
        }
        return orderBy;
    }
}
