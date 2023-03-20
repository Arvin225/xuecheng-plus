package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 课程基本信息
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:50:46
 */
@Getter
@Setter
@TableName("course_base")
@ApiModel(value = "CourseBase对象", description = "课程基本信息")
public class CourseBase {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("机构ID")
    @TableField("company_id")
    private Long companyId;

    @ApiModelProperty("机构名称")
    @TableField("company_name")
    private String companyName;

    @ApiModelProperty("课程名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("适用人群")
    @TableField("users")
    private String users;

    @ApiModelProperty("课程标签")
    @TableField("tags")
    private String tags;

    @ApiModelProperty("大分类")
    @TableField("mt")
    private String mt;

    @ApiModelProperty("小分类")
    @TableField("st")
    private String st;

    @ApiModelProperty("课程等级")
    @TableField("grade")
    private String grade;

    @ApiModelProperty("教育模式(common普通，record 录播，live直播等）")
    @TableField("teachmode")
    private String teachmode;

    @ApiModelProperty("课程介绍")
    @TableField("description")
    private String description;

    @ApiModelProperty("课程图片")
    @TableField("pic")
    private String pic;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    @ApiModelProperty("修改时间")
    @TableField(value = "change_date", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime changeDate;

    @ApiModelProperty("创建人")
    @TableField("create_people")
    private String createPeople;

    @ApiModelProperty("更新人")
    @TableField("change_people")
    private String changePeople;

    @ApiModelProperty("审核状态")
    @TableField("audit_status")
    private String auditStatus;

    @ApiModelProperty("课程发布状态 未发布  已发布 下线")
    @TableField("status")
    private String status;


}
