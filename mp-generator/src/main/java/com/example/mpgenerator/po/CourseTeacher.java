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
 * 课程-教师关系表
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:50:46
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
