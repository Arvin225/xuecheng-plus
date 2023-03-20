package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 课程分类
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:50:46
 */
@Getter
@Setter
@TableName("course_category")
@ApiModel(value = "CourseCategory对象", description = "课程分类")
public class CourseCategory {

    @ApiModelProperty("主键")
    @TableId("id")
    private String id;

    @ApiModelProperty("分类名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("分类标签默认和名称一样")
    @TableField("label")
    private String label;

    @ApiModelProperty("父结点id（第一级的父节点是0，自关联字段id）")
    @TableField("parentid")
    private String parentid;

    @ApiModelProperty("是否显示")
    @TableField("is_show")
    private Integer isShow;

    @ApiModelProperty("排序字段")
    @TableField("orderby")
    private Integer orderby;

    @ApiModelProperty("是否叶子")
    @TableField("is_leaf")
    private Integer isLeaf;


}
