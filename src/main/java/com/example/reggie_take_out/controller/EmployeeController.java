package com.example.reggie_take_out.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.CommonsConst;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Employee;
import com.example.reggie_take_out.param.EmployeeLoginParam;
import com.example.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody @Validated EmployeeLoginParam loginParam) {
       return employeeService.login(request,loginParam);
    }

    @PostMapping
    public R register(HttpServletRequest request,@RequestBody @Validated Employee employee){
        log.info("新增员工信息：{}", employee.toString());
        // 设置默认密码为123456 并进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(CommonsConst.INIT_PASSWORD.getBytes()));
//         //设置创建时间
//        employee.setCreateTime(new Date());
//         //设置更新时间
//        employee.setUpdateTime(new Date());
//         //用户ID设置（session中取得）
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
         //调用存储方法
        employeeService.save(employee);
        return R.success("添加成功");
    }

    @PostMapping("/logout")
    public R logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("登出成功");
    }

    @GetMapping("/page")
    public R<Page> page(@RequestParam(value = "page",defaultValue = "1") Integer page,
                        @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
                        @RequestParam(value = "name",required = false) String name){
        Page pageInfo=employeeService.page(page,pageSize,name);
        return R.success(pageInfo);
    }

    @GetMapping("/{id}")
    public R getOne(@PathVariable Long id){
        return R.success(employeeService.getById(id));
    }


    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        long id = Thread.currentThread().getId();
        log.info("线程ID为：{}", id);
//        // 获取员工ID
//         Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(new Date());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

}

