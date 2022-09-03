package com.example.reggie_take_out.param;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CategoryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //@NotNull(message = "不能为0")
    @Range(min = 1,max = 2,message = "必须在1到2之间")
    //类型 1 菜品分类 2 套餐分类
    private Integer type;

    @NotBlank(message = "分类名字不能为空")
    //分类名称
    private String name;

    @Min(value = 0,message = "排序必须大于等于0")
    //顺序
    private Integer sort;


}
