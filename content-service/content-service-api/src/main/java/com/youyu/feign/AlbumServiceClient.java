package com.youyu.feign;

import com.youyu.dto.album.AlbumDTO;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "content-service")
public interface AlbumServiceClient {

    /**
     * 根据相册ID查询相册信息
     *
     * @param albumId 相册ID
     * @return 相册信息
     */
    @PostMapping(value = "/album/open/getById")
    ResponseResult<AlbumDTO> getById(@RequestParam Long albumId);
}