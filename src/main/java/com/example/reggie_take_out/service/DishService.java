package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.param.dto.DishDTO;

import java.util.List;

/**
 * <p>
 * 菜品管理 服务类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
public interface DishService extends IService<Dish> {

    Page page(Integer page, Integer pageSize, String name);

    DishDTO getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDTO dishDTO);

    void saveWithFlover(DishDTO dishDTO);

    List<DishDTO> list(Dish dish,String name);

    void removeWithDish(List<Long> ids);
}
