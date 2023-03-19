package com.example.mpgenerator.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 课程营销信息
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:50:46
 */
@Getter
@Setter
@TableName("course_market")
@ApiModel(value = "CourseMarket对象", description = "课程营销信息")
public class CourseMarket {

    @ApiModelProperty("主键，课程id")
    @TableId("id")
    private Long id;

    @ApiModelProperty("收费规则，对应数据字典")
    @TableField("charge")
    private String charge;

    @ApiModelProperty("现价")
    @TableField("price")
    private Float price;

    @ApiModelProperty("原价")
    @TableField("original_price")
    private Float originalPrice;

    @ApiModelProperty("咨询qq")
    @TableField("qq")
    private String qq;

    @ApiModelProperty("微信")
    @TableField("wechat")
    private String wechat;

    @ApiModelProperty("电话")
    @TableField("phone")
    private String phone;

    @ApiModelProperty("有效期天数")
    @TableField("valid_days")
    private Integer validDays;


}
