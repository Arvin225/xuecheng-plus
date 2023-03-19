package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:27:59
 */
@Getter
@Setter
@TableName("teachplan_media")
@ApiModel(value = "TeachplanMedia对象", description = "")
public class TeachplanMedia {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("媒资文件id")
    @TableField("media_id")
    private String mediaId;

    @ApiModelProperty("课程计划标识")
    @TableField("teachplan_id")
    private Long teachplanId;

    @ApiModelProperty("课程标识")
    @TableField("course_id")
    private Long courseId;

    @ApiModelProperty("媒资文件原始名称")
    @TableField("media_fileName")
    private String mediaFilename;

    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    @ApiModelProperty("创建人")
    @TableField("create_people")
    private String createPeople;

    @ApiModelProperty("修改人")
    @TableField("change_people")
    private String changePeople;


}
