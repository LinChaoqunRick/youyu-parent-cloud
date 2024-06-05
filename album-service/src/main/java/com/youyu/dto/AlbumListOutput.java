package com.youyu.dto;

import com.youyu.entity.Album;
import com.youyu.entity.user.User;
import lombok.Data;

import java.util.List;

@Data
public class AlbumListOutput extends Album {
    private Long imageCount;
    List<User> authorizedUserList;
}
