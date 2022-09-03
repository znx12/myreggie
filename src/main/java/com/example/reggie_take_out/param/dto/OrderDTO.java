package com.example.reggie_take_out.param.dto;

import com.example.reggie_take_out.entity.OrderDetail;
import com.example.reggie_take_out.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO extends Orders {
    // 用户名
    private String userName;

    // 手机号
    private String phone;

    // 地址
    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
}
