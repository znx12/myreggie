package com.example.reggie_take_out.service;

import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 购物车 服务类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    //加入购物车
    ShoppingCart add(ShoppingCart shoppingCart);

    //根据用户id查询购物车
    List<ShoppingCart> getByUserId();

    R sub(ShoppingCart shoppingCart);
}
