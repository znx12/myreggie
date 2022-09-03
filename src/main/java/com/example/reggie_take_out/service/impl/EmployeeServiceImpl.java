package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.CommonsConst;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Employee;
import com.example.reggie_take_out.mapper.EmployeeMapper;
import com.example.reggie_take_out.param.EmployeeLoginParam;
import com.example.reggie_take_out.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {



    //登陆
    @Override
    public R<Employee> login(HttpServletRequest request, EmployeeLoginParam loginParam) {
        String password = loginParam.getPassword();
        String username = loginParam.getUsername();
        password = DigestUtils.md5Hex(password);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, username);
        queryWrapper.last("limit 1");
        queryWrapper.eq(Employee::getStatus,1);
        Employee emp = getOne(queryWrapper);
        // 查不到返回登录失败结果
        if (emp == null) {
            return R.error(CommonsConst.LOGIN_FAIL);
        }
        // 比对密码
        if (!emp.getPassword().equals(password)) {
            return R.error(CommonsConst.LOGIN_FAIL);
        }
        // 登录成功将员工的ID放入session中
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }
    //分页查询员工
    @Override
    public Page page(Integer page, Integer pageSize, String name) {
        Page pageInfo=new Page(page,pageSize);
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(name)){
            queryWrapper
                    .like(Employee::getUsername,name)
                    .or()
                    .like(Employee::getName,name);
        }
        queryWrapper.orderByDesc(Employee::getStatus,Employee::getUpdateTime);
        return this.page(pageInfo,queryWrapper);
    }
}
