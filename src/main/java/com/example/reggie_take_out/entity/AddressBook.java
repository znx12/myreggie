package com.example.reggie_take_out.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 地址管理
 * </p>
 *
 * @author znx
 * @since 2022-08-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("address_book")
@ApiModel(value="AddressBook对象", description="地址管理")
public class AddressBook implements Serializable {

    private static final long serialVersionUID=1L;
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键")
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "收货人")
    private String consignee;

    @ApiModelProperty(value = "性别 0 女 1 男")
    private Integer sex;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "省级区划编号")
    private String provinceCode;

    @ApiModelProperty(value = "省级名称")
    private String provinceName;

    @ApiModelProperty(value = "市级区划编号")
    private String cityCode;

    @ApiModelProperty(value = "市级名称")
    private String cityName;

    @ApiModelProperty(value = "区级区划编号")
    private String districtCode;

    @ApiModelProperty(value = "区级名称")
    private String districtName;

    @ApiModelProperty(value = "详细地址")
    private String detail;

    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "默认 0 否 1是")
    private Boolean isDefault;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "修改人")
    private Long updateUser;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted;



}
