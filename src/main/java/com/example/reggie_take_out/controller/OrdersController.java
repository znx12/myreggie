package com.example.reggie_take_out.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Orders;
import com.example.reggie_take_out.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 管理员查询订单
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R Page(int page,int pageSize,Long number,String beginTime,String endTime,String name){
        Page<Orders> ordersPage = new Page(page, pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //增加查询条件
        queryWrapper.like(number != null, Orders::getNumber, number)
                .like(name!=null,Orders::getUserName,name)
                .gt(StringUtils.isNotEmpty(beginTime), Orders::getOrderTime, beginTime)
                .lt(StringUtils.isNotEmpty(endTime), Orders::getOrderTime, endTime);

        ordersService.page(ordersPage, queryWrapper);
        return R.success(ordersPage);
    }

    /**
     * 管理员更改订单状态为已经派送==3
     *
     * @param orders
     * @return
     */
    @PutMapping
    public R<Orders> updateStatus(@RequestBody Orders orders) {

        Integer status = orders.getStatus();
        if (status != null) {
            orders.setStatus(3);
        }
        ordersService.updateById(orders);
        return R.success(orders);
    }

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功!");
    }

    /**
     * 用户查询自己订单 分页
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page,int pageSize){
        return R.success(ordersService.orderPage(page,pageSize));
    }







}

