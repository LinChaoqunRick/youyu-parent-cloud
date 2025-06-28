package com.youyu.dto.post.favorites;

import com.youyu.dto.post.post.PostListOutput;
import com.youyu.entity.post.Favorites;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FavoritesListOutput extends Favorites {
    private Long count;
    private List<PostListOutput> previewPosts;
}
