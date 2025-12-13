package com.youyu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileMenuDTO {
    private Long id;
    private Long userId;
    private Integer showHome = 1;
    private Integer showMoment = 1;
    private Integer showPost = 1;
    private Integer showNote = 1;
    private Integer showColumn = 1;
    private Integer showFavorites = 1;
    private Integer showFollow = 1;
    private Integer showFans = 1;
    private Integer showAlbum = 1;
}
