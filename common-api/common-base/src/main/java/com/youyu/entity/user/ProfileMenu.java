package com.youyu.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * (ProfileMenu)表实体类
 *
 * @author makejava
 * @since 2023-05-07 12:13:44
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("bs_profile_menu")
public class ProfileMenu implements Serializable {
    //主键id
    private Long id;
    //用户id
    @NotNull(message = "用户Id不能为空")
    private Long userId;
    @NotNull(message = "showHome不能为空")
    private Integer showHome = 1;
    @NotNull(message = "showMoment不能为空")
    private Integer showMoment = 1;
    @NotNull(message = "showPost不能为空")
    private Integer showPost = 1;
    @NotNull(message = "showNote不能为空")
    private Integer showNote = 1;
    @NotNull(message = "showColumn不能为空")
    private Integer showColumn = 1;
    @NotNull(message = "showCollect不能为空")
    private Integer showCollect = 1;
    @NotNull(message = "showFollow不能为空")
    private Integer showFollow = 1;
    @NotNull(message = "showFans不能为空")
    private Integer showFans = 1;

}

