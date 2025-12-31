package com.youyu.dto.album;

import lombok.Data;

import java.io.Serializable;

/**
 * 相册DTO（用于Feign接口传输）
 *
 * @author youyu
 */
@Data
public class AlbumDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 相册ID
     */
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 相册名称
     */
    private String name;
}