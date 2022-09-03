package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.param.CategoryParam;

import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
public interface CategoryService extends IService<Category> {
    Page page(Integer page,Integer pageSize);

    void save(CategoryParam categoryParam);

    void update(CategoryParam categoryParam);

    void remove(Long ids);

    List<Category> list(Category category);
}
