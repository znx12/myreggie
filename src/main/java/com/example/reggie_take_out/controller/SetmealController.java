package com.example.reggie_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.common.aop.LogAnnotation;
import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.entity.SetmealDish;
import com.example.reggie_take_out.param.dto.SetMealDTO;
import com.example.reggie_take_out.service.SetmealDishService;
import com.example.reggie_take_out.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 套餐 前端控制器
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 套餐分页查询
     */
    @LogAnnotation(module = "套餐管理",operation = "分页查询套餐")
    @GetMapping("/page")
    public R<Page> page(@RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                        @RequestParam(value = "name", required = false) String name){
        Page setMealDtoList=setmealService.page(page,pageSize,name);
        return R.success(setMealDtoList);
    }

    /**
     * 根据id查询套餐信息
     */
    @LogAnnotation(module = "套餐管理",operation = "查询单个套餐")
    @GetMapping("/{id}")
    public R<SetMealDTO> getById(@PathVariable Long id){
        SetMealDTO setmealDto=setmealService.selectById(id);
        return R.success(setmealDto);
    }

    /**
     * 保存套餐
     */
    @LogAnnotation(module = "套餐管理",operation = "保存套餐")
    @PostMapping()
    public R save(@RequestBody SetMealDTO setMealDTO){
        setmealService.saveDto(setMealDTO);
        return R.success("套餐保存成功");
    }

    /**
     * 修改套餐
     */
    @LogAnnotation(module = "套餐管理", operation = "修改套餐")
    @PutMapping
    public R update(@RequestBody SetMealDTO setMealDTO){
        setmealService.updateDto(setMealDTO);
        return R.success("修改套餐成功");
    }



    /**
     * 修改套餐的销售状态
     *
     */
    @PostMapping("/status/{status}")
    @LogAnnotation(module = "套餐管理", operation = "修改套餐的销售状态")
    public R updateStatus(@PathVariable Integer status,Long[] ids){
        for (int i=0;i< ids.length;i++){
            Setmeal setmeal = setmealService.getById(ids[i]);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("修改套餐状态成功");
    }

    /**
     * 根据条件查询套餐数据
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        // 构造查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 删除套餐
     */
    @LogAnnotation(module = "套餐管理",operation = "删除套餐及其菜品")
    @DeleteMapping
    public R delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }





}

