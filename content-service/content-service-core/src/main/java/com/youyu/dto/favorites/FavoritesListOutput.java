package com.youyu.dto.favorites;

import com.youyu.dto.post.PostListOutput;
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
