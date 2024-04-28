package com.youyu.entity.post;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * (Favorites)表实体类
 *
 * @author makejava
 * @since 2024-04-28 22:40:14
 */
@SuppressWarnings("serial")
@Data
public class Favorites extends Model<Favorites> {
    //主键
    @TableId
    private Long id;
    //归属用户id
    private Long userId;
    //收藏夹名称
    @NotBlank(message = "收藏夹名称不能为空")
    private String name;
    //收藏的博客id
    private String postIds;
    //是否公开，1：是，0否
    private Integer open;
    //是否置顶
    private Integer isTop;
    //点赞数量
    private Long likeCount;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //删除标志
    private Integer deleted;
}

