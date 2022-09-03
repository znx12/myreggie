package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.BaseContext;
import com.example.reggie_take_out.common.CustomException;
import com.example.reggie_take_out.entity.*;
import com.example.reggie_take_out.mapper.OrdersMapper;
import com.example.reggie_take_out.param.dto.OrderDTO;
import com.example.reggie_take_out.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {


    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private UserService userService;

    @Resource
    private AddressBookService addressBookService;

    @Resource
    private OrderDetailService orderDetailService;

    @Transactional
    @Override
    public void submit(Orders orders) throws CustomException {
        //获取当前用户
        Long currentId = BaseContext.getCurrentId();
        orders.setUserId(currentId);
        //获取当前用户的购物车数量
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if(shoppingCartList==null||shoppingCartList.size()==0){
            throw new CustomException("购物车为空 不能下单");
        }
        // 查询用户数据
        User user = userService.getById(currentId);
        // 查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("地址为空，不能下单");
        }
        // 订单号
        long orderId = IdWorker.getId();
        // 计算购物车总金额 保证多线程情况下线程安全计算
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        // 设置订单信息
        orders.setId(orderId);
        orders.setOrderTime(new Date());
        orders.setCheckoutTime(new Date());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);

    }

    @Override
    public Page orderPage(int page, int pageSize) {
        Page<Orders> orderPage=new Page<>(page,pageSize);
        Page<OrderDTO> orderDTOPage=new Page<>(page,pageSize);

        //构建查询条件
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        this.page(orderPage,queryWrapper);
        BeanUtils.copyProperties(orderPage,orderDTOPage,"records");
        orderDTOPage.setRecords(copyList(orderPage.getRecords()));
        return orderDTOPage;
    }

    private List<OrderDTO> copyList(List<Orders> records) {
        List<OrderDTO> orderDTOList=new ArrayList<>();
        for (Orders record : records) {
            orderDTOList.add(copy(record));
        }
        return orderDTOList;
    }

    private OrderDTO copy(Orders record) {
        OrderDTO orderDTO=new OrderDTO();
        LambdaQueryWrapper<OrderDetail> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,record.getId());
        List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);
        BeanUtils.copyProperties(record,orderDTO);
        orderDTO.setOrderDetails(orderDetails);
        return orderDTO;
    }
}
