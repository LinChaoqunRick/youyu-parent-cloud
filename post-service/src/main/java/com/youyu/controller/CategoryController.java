package com.youyu.controller;

import com.youyu.result.ResponseResult;
import com.youyu.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * (BsCategory)表控制层
 *
 * @author makejava
 * @since 2023-01-01 22:31:12
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public ResponseResult list() {
        return ResponseResult.success(categoryService.list());
    }
}

