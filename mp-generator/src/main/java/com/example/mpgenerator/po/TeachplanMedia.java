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
 * 
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:50:46
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
