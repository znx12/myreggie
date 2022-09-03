package com.example.reggie_take_out.service.impl;

import com.example.reggie_take_out.entity.OrderDetail;
import com.example.reggie_take_out.mapper.OrderDetailMapper;
import com.example.reggie_take_out.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
