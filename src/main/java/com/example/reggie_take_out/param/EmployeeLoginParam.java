package com.example.reggie_take_out.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



import javax.validation.constraints.NotBlank;
@Data
public class EmployeeLoginParam {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空！")
    private String username;

    /**
     * 姓名
     */
    @NotBlank(message = "密码不能为空！")
    @ApiModelProperty(value = "密码")
    private String password;
}
