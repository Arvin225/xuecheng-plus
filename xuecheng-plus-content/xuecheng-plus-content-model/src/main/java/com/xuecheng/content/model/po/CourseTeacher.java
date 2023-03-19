package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 课程-教师关系表
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:27:59
 */
@Getter
@Setter
@TableName("course_teacher")
@ApiModel(value = "CourseTeacher对象", description = "课程-教师关系表")
public class CourseTeacher {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("课程标识")
    @TableField("course_id")
    private Long courseId;

    @ApiModelProperty("教师标识")
    @TableField("teacher_name")
    private String teacherName;

    @ApiModelProperty("教师职位")
    @TableField("position")
    private String position;

    @ApiModelProperty("教师简介")
    @TableField("introduction")
    private String introduction;

    @ApiModelProperty("照片")
    @TableField("photograph")
    private String photograph;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;


}
