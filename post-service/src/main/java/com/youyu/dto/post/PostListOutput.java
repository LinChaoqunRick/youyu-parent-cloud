package com.youyu.dto.post;

import com.youyu.dto.user.UserOutput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostListOutput implements Serializable {
    private Long id;
    private Long userId;
    private PostUserOutput user;
    //标题
    private String title;
    //文章摘要
    private String summary;
    //
    private String createType;
    //所属分类Id
    private Long categoryId;
    private String categoryName;
    //缩略图地址
    private String thumbnail;
    //评论数量
    private Integer commentCount;
    //访问量
    private Integer viewCount;
    //专栏Ids
    private String columnIds;

    private Date createTime;
}
