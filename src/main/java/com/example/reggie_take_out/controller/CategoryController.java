package com.example.reggie_take_out.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.common.aop.LogAnnotation;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.param.CategoryParam;
import com.example.reggie_take_out.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @LogAnnotation(module = "套餐分类",operation = "分页查询分类")
    @GetMapping("/page")
    public R<Page> page(@RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize){
        Page pageInfo=categoryService.page(page,pageSize);
        return R.success(pageInfo);
    }

    @PostMapping
    @LogAnnotation(module = "套餐分类",operation = "增加分类")
    public R save(@Valid @RequestBody CategoryParam categoryParam){
        categoryService.save(categoryParam);
        return R.success("增加分类成功");
    }

    @PutMapping
    @LogAnnotation(module = "套餐分类", operation = "修改分类")
    public R update(@Valid @RequestBody CategoryParam categoryParam) {
        categoryService.update(categoryParam);
        return R.success("修改分类成功");
    }

    @LogAnnotation(module = "套餐分类", operation = "删除分类")
    @DeleteMapping
    public R delete(Long id){
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    @GetMapping("/list")
    public R list(Category category){
        List<Category> categoryList=categoryService.list(category);
        return R.success(categoryList);
    }






}

