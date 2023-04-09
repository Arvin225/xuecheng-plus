package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@ApiModel(value = "CoursePreviewDto", description = "课程预览数据模型")
public class CoursePreviewDto {

    @ApiModelProperty("课程基本信息、课程营销信息")
    CourseBaseInfoDto courseBase;

    @ApiModelProperty("课程计划信息")
    List<TeachplanDto> teachplans;

    //todo：课程师资信息

}
