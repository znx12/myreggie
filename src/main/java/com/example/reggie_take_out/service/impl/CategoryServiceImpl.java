package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.CustomException;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.mapper.CategoryMapper;
import com.example.reggie_take_out.param.CategoryParam;
import com.example.reggie_take_out.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.service.DishService;
import com.example.reggie_take_out.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 服务实现类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService mealService;


    @Override
    public Page page(Integer page, Integer pageSize) {
        // 分页构造
        Page<Category> pageInfo = new Page<Category>(page, pageSize);
        // 查询并排序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.orderByAsc(Category::getSort);
        // 分页查询
        return this.page(pageInfo, queryWrapper);
    }

    @Override
    public void save(CategoryParam categoryParam) {
        Category category=new Category();
        BeanUtils.copyProperties(categoryParam,category);
        save(category);
    }

    @Override
    public void update(CategoryParam categoryParam) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryParam, category);
        updateById(category);
    }


    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> dishQueryWrapper=new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId,ids);
        int dishCount = dishService.count(dishQueryWrapper);
        if(dishCount>0){
            throw new CustomException("该分类下仍然有菜品 无法删除！");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int  mealCount= mealService.count(setmealLambdaQueryWrapper);
        if (mealCount > 0) {
            // 已经关联套餐，抛出异常
            throw new CustomException("当前分类已关联套餐，不可删除");
        }
        //直接删除
        removeById(ids);
    }

    @Override
    public List<Category> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        if(category.getType()!=null) {
            queryWrapper.eq(Category::getType, category.getType());
        }
        queryWrapper.orderByAsc(Category::getSort)
                .or().orderByDesc(Category::getUpdateTime);

        return list(queryWrapper);
    }
}
