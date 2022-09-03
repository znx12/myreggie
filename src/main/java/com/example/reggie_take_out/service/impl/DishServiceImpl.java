package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.CommonsConst;
import com.example.reggie_take_out.common.CustomException;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.DishFlavor;
import com.example.reggie_take_out.mapper.CategoryMapper;
import com.example.reggie_take_out.mapper.DishFlavorMapper;
import com.example.reggie_take_out.mapper.DishMapper;
import com.example.reggie_take_out.mapper.SetmealDishMapper;
import com.example.reggie_take_out.param.dto.DishDTO;
import com.example.reggie_take_out.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 服务实现类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper mealDishMapper;



    //分页查询
    @Override
    public Page page(Integer page, Integer pageSize, String name) {
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        Page<Dish> dishPage=new Page<>(page,pageSize);
        Page<Dish> dishList = this.page(dishPage, queryWrapper);

        Page<DishDTO> dishDtoPage = new Page(page, pageSize);
        BeanUtils.copyProperties(dishList, dishDtoPage, "records");
        List<DishDTO> dishDTOList=copyList(dishList.getRecords());
        dishDtoPage.setRecords(dishDTOList);
        return dishDtoPage;
    }

    //根据id查询单个
    @Override
    public DishDTO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.selectById(id);
        return copy(dish);
    }

    //更新菜品
    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        this.updateById(dishDTO);
        //清理当前菜品对应的口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDTO.getId());
        dishFlavorMapper.delete(queryWrapper);
        //插入
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDTO.getId());
            return item;
        }).collect(Collectors.toList());
        for(DishFlavor flavor:flavors) {
            dishFlavorMapper.insert(flavor);
        }
    }

    //保存菜品
    @Override
    public void saveWithFlover(DishDTO dishDTO) {
        this.save(dishDTO);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDTO.getId());
            return item;
        }).collect(Collectors.toList());
        for (DishFlavor flavor : flavors) {
            dishFlavorMapper.insert(flavor);
        }
    }

    @Override
    public List<DishDTO> list(Dish dish,String name) {
        // 构造条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Dish::getStatus, CommonsConst.DISH_OPEN);
        if(dish.getCategoryId() != null) {
            queryWrapper.eq( Dish::getCategoryId, dish.getCategoryId());
        }
        if(StringUtils.isNotEmpty(name))
        {
            queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        };
        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = this.list(queryWrapper);
        return copyList(dishList);
    }

    @Override
    public void removeWithDish(List<Long> ids) {
        // 查询菜品状态确定是否可以删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(queryWrapper);
        // 查到起售数据
        if (count > 0) {
            throw new CustomException("菜品正在售卖,无法删除");
        }
        // 可以删除，先删除菜品对应的口味表数据
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorMapper.delete(dishFlavorLambdaQueryWrapper);
        // 再删除菜品
        this.removeByIds(ids);
    }

    //复制DtoList
    private List<DishDTO> copyList(List<Dish> records) {
        List<DishDTO> dishDTOList=new ArrayList<>();
        for(Dish dish:records){
            dishDTOList.add(copy(dish));
        }
        return dishDTOList;
    }

    //复制Dto
    private DishDTO copy(Dish dish) {
        DishDTO dishDTO=new DishDTO();
        BeanUtils.copyProperties(dish,dishDTO);
        //给categoryName赋值
        Long categoryId = dish.getCategoryId();
        Category category = categoryMapper.selectById(categoryId);
        if(category!=null){
            dishDTO.setCategoryName(category.getName());
        }
        //给菜的口味赋值
        Long dishId = dish.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(queryWrapper);
        dishDTO.setFlavors(dishFlavors);

        return dishDTO;
    }


}
