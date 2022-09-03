package com.example.reggie_take_out.controller;


import cn.hutool.core.lang.PatternPool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.reggie_take_out.common.CommonsConst;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.common.ValidateCodeUtils;
import com.example.reggie_take_out.entity.User;
import com.example.reggie_take_out.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 移动端发送短信
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();
        Pattern mobile= PatternPool.MOBILE;
        Matcher matcher=mobile.matcher(phone);
        if(!matcher.matches()){
            return R.error("手机号格式有误");
        }
        if(StringUtils.isNotEmpty(phone)){
            //生成验证码
            String code= ValidateCodeUtils.generateValidateCode4String(4);
            log.info(code);
            session.setAttribute(phone,code);
            return R.success("短信发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     */
    @PostMapping("/login")
    @Transactional
    public R<User> login(@RequestBody Map map, HttpSession session){
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");
        Object codeSession = session.getAttribute(phone);
        //比对验证码
        if(codeSession!=null&&codeSession.equals(code)){
            //成功则登录
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            //新用户 自动完成注册
            User user = userService.getOne(queryWrapper);
            if(user==null){
                user=new User();
                user.setPhone(phone);
                user.setSex("1");
                user.setName("测试");
                user.setStatus(CommonsConst.EMPLOYEE_STATUS_YES);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败 验证码有误");
    }


    /**
     * 退出功能
     * ①在controller中创建对应的处理方法来接受前端的请求，请求方式为post；
     * ②清理session中的用户id
     * ③返回结果（前端页面会进行跳转到登录页面）
     *
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        //清理session中的用户id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }


}

