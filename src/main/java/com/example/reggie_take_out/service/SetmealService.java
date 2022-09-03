package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.param.dto.SetMealDTO;

import java.util.List;

/**
 * <p>
 * 套餐 服务类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 分页查询套餐
     */
    Page page(Integer page, Integer pageSize, String name);

    /**
     *
     *根据id查询套餐
     */
    SetMealDTO selectById(Long id);

    /**
     * 保存套餐
     */
    void saveDto(SetMealDTO setMealDTO);

    /**
     *修改套餐
     */
    void updateDto(SetMealDTO setMealDTO);

    /**
     *删除套餐
     */
    void removeWithDish(List<Long> ids);
}
