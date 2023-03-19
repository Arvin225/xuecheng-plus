package com.example.mpgenerator.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * 课程计划
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:50:46
 */
@Getter
@Setter
@TableName("teachplan")
@ApiModel(value = "Teachplan对象", description = "课程计划")
public class Teachplan {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("课程计划名称")
    @TableField("pname")
    private String pname;

    @ApiModelProperty("课程计划父级Id")
    @TableField("parentid")
    private Long parentid;

    @ApiModelProperty("层级，分为1、2、3级")
    @TableField("grade")
    private Integer grade;

    @ApiModelProperty("课程类型:1视频、2文档")
    @TableField("media_type")
    private String mediaType;

    @ApiModelProperty("开始直播时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty("直播结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty("章节及课程时介绍")
    @TableField("description")
    private String description;

    @ApiModelProperty("时长，单位时:分:秒")
    @TableField("timelength")
    private String timelength;

    @ApiModelProperty("排序字段")
    @TableField("orderby")
    private Integer orderby;

    @ApiModelProperty("课程标识")
    @TableField("course_id")
    private Long courseId;

    @ApiModelProperty("课程发布标识")
    @TableField("course_pub_id")
    private Long coursePubId;

    @ApiModelProperty("状态（1正常  0删除）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("是否支持试学或预览（试看）")
    @TableField("is_preview")
    private String isPreview;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    @ApiModelProperty("修改时间")
    @TableField(value = "change_date", fill = FieldFill.INSERT_UPDATE)
    private Date changeDate;


}
