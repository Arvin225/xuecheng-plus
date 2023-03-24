package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(value="EditCourseDto", description="修改课程基本信息")
public class EditCourseDto extends AddCourseDto{
    @ApiModelProperty(value = "课程id", required = true)
    @NotEmpty(message = "课程id不能为空")
    private Long id;
}
