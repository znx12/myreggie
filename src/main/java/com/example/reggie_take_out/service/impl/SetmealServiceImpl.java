package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.CustomException;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.entity.SetmealDish;
import com.example.reggie_take_out.mapper.CategoryMapper;
import com.example.reggie_take_out.mapper.SetmealDishMapper;
import com.example.reggie_take_out.mapper.SetmealMapper;
import com.example.reggie_take_out.param.dto.SetMealDTO;
import com.example.reggie_take_out.service.CategoryService;
import com.example.reggie_take_out.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 套餐 服务实现类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    //分页查询套餐
    @Override
    public Page page(Integer page, Integer pageSize, String name) {
        //构建分页对象
        Page<Setmeal> setmealPage=new Page<>();
        Page<SetMealDTO> setMealDTOPage=new Page<>();
        //分页查询setmeal
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        this.page(setmealPage,queryWrapper);
        //复制
        BeanUtils.copyProperties(setmealPage,setMealDTOPage,"records");

        List<SetMealDTO> setMealDTOList=copyList(setmealPage.getRecords());
        setMealDTOPage.setRecords(setMealDTOList);
        return setMealDTOPage;
    }

    @Override
    public SetMealDTO selectById(Long id) {
        Setmeal setMeal = getById(id);
        return copy(setMeal,true);
    }

    //涉及到多个表 事务操作
    @Transactional
    @Override
    public void saveDto(SetMealDTO setMealDTO) {
        //保存套餐基本信息
        this.save(setMealDTO);
        for (SetmealDish setmealDish : setMealDTO.getSetmealDishes()) {
            setmealDish.setSetmealId(String.valueOf(setMealDTO.getId()));
            setmealDishMapper.insert(setmealDish);
        }
    }

    @Transactional
    @Override
    public void updateDto(SetMealDTO setMealDTO) {
        this.updateById(setMealDTO);
        //先删除原来套餐里面的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setMealDTO.getId());
        setmealDishMapper.delete(queryWrapper);
        //保存现在套餐的菜品
        for (SetmealDish setmealDish : setMealDTO.getSetmealDishes()) {
            setmealDish.setSetmealId(String.valueOf(setMealDTO.getId()));
            setmealDishMapper.insert(setmealDish);
        }
    }

    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {

        //先判断套餐是否是停售状态
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper=new LambdaQueryWrapper<>();
        setmealQueryWrapper.in(Setmeal::getId,ids);
        setmealQueryWrapper.eq(Setmeal::getStatus,1);
        int count = count(setmealQueryWrapper);
        if(count>0){
            throw  new CustomException("套餐未停售 不能删除!");
        }
        //再删除
        for (Long id:ids){
            this.removeById(id);
            //删除套餐里面的菜品
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, id);
            setmealDishMapper.delete(queryWrapper);
        }
    }

    //复制list
    private List<SetMealDTO> copyList(List<Setmeal> records) {
        List<SetMealDTO> setMealDTOList = new ArrayList<>();
        for(Setmeal record:records){
            setMealDTOList.add(copy(record,false));
        }
        return setMealDTOList;
    }

    //复制dto 是否需要套餐下的菜品
    private SetMealDTO copy(Setmeal record,Boolean isDish) {
        SetMealDTO setMealDTO=new SetMealDTO();
        BeanUtils.copyProperties(record,setMealDTO);
        Long categoryId = record.getCategoryId();
        Category category = categoryMapper.selectById(categoryId);
        if(category!=null){
            setMealDTO.setCategoryName(category.getName());
        }
        //需要菜品
        if(isDish){
            Long setMealId = record.getId();
            LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,setMealId);
            List<SetmealDish> setmealDishes = setmealDishMapper.selectList(queryWrapper);
            setMealDTO.setSetmealDishes(setmealDishes);
        }
        return setMealDTO;

    }
}
