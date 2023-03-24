package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="TeachplanDto", description="课程计划信息")
public class TeachplanDto extends Teachplan {

    @ApiModelProperty(value = "课程计划的子节点")
    private List<TeachplanDto> teachPlanTreeNodes;

    @ApiModelProperty(value = "课程计划媒资")
    private TeachplanMedia teachplanMedia;
}
