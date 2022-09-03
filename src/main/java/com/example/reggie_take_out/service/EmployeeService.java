package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Employee;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.param.EmployeeLoginParam;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 员工信息 服务类
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
public interface EmployeeService extends IService<Employee> {

    R<Employee> login(HttpServletRequest request, EmployeeLoginParam loginParam);

    Page page(Integer page, Integer pageSize, String name);
}
