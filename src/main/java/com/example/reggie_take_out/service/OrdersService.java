package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);

    Page orderPage(int page, int pageSize);
}
