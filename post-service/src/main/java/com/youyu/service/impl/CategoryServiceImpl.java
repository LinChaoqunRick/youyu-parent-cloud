package com.youyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.mapper.CategoryMapper;
import com.youyu.entity.Category;
import com.youyu.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * (BsCategory)表服务实现类
 *
 * @author makejava
 * @since 2023-01-01 22:31:17
 */
@Service("bsCategoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}

