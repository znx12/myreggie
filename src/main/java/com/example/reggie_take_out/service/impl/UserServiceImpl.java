package com.example.reggie_take_out.service.impl;

import com.example.reggie_take_out.entity.User;
import com.example.reggie_take_out.mapper.UserMapper;
import com.example.reggie_take_out.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
