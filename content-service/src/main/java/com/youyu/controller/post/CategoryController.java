package com.youyu.controller.post;

import com.youyu.entity.post.Category;
import com.youyu.result.ResponseResult;
import com.youyu.service.post.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * (BsCategory)表控制层
 *
 * @author makejava
 * @since 2023-01-01 22:31:12
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @GetMapping("/open/list")
    public ResponseResult<List<Category>> list() {
        List<Category> categoryList = categoryService.list();
        return ResponseResult.success(categoryList);
    }
}

