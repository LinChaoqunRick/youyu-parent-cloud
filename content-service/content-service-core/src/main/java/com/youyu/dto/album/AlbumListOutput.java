package com.youyu.dto.album;

import com.youyu.dto.UserDTO;
import com.youyu.dto.post.PostUserOutput;
import com.youyu.entity.album.Album;
import lombok.Data;

import java.util.List;

@Data
public class AlbumListOutput extends Album {
    private Long imageCount;
    private PostUserOutput userInfo;
    List<UserDTO> authorizedUserList;
}
