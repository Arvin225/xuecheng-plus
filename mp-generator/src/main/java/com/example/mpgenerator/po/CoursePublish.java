package com.example.mpgenerator.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 课程发布
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:50:46
 */
@Getter
@Setter
@TableName("course_publish")
@ApiModel(value = "CoursePublish对象", description = "课程发布")
public class CoursePublish {

    @ApiModelProperty("主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty("机构ID")
    @TableField("company_id")
    private Long companyId;

    @ApiModelProperty("公司名称")
    @TableField("company_name")
    private String companyName;

    @ApiModelProperty("课程名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("适用人群")
    @TableField("users")
    private String users;

    @ApiModelProperty("标签")
    @TableField("tags")
    private String tags;

    @ApiModelProperty("创建人")
    @TableField("username")
    private String username;

    @ApiModelProperty("大分类")
    @TableField("mt")
    private String mt;

    @ApiModelProperty("大分类名称")
    @TableField("mt_name")
    private String mtName;

    @ApiModelProperty("小分类")
    @TableField("st")
    private String st;

    @ApiModelProperty("小分类名称")
    @TableField("st_name")
    private String stName;

    @ApiModelProperty("课程等级")
    @TableField("grade")
    private String grade;

    @ApiModelProperty("教育模式")
    @TableField("teachmode")
    private String teachmode;

    @ApiModelProperty("课程图片")
    @TableField("pic")
    private String pic;

    @ApiModelProperty("课程介绍")
    @TableField("description")
    private String description;

    @ApiModelProperty("课程营销信息，json格式")
    @TableField("market")
    private String market;

    @ApiModelProperty("所有课程计划，json格式")
    @TableField("teachplan")
    private String teachplan;

    @ApiModelProperty("教师信息，json格式")
    @TableField("teachers")
    private String teachers;

    @ApiModelProperty("发布时间")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    @ApiModelProperty("上架时间")
    @TableField("online_date")
    private Date onlineDate;

    @ApiModelProperty("下架时间")
    @TableField("offline_date")
    private Date offlineDate;

    @ApiModelProperty("发布状态")
    @TableField("status")
    private String status;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("收费规则，对应数据字典--203")
    @TableField("charge")
    private String charge;

    @ApiModelProperty("现价")
    @TableField("price")
    private Float price;

    @ApiModelProperty("原价")
    @TableField("original_price")
    private Float originalPrice;

    @ApiModelProperty("课程有效期天数")
    @TableField("valid_days")
    private Integer validDays;


}
