package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Api(value = "课程信息编辑接口", tags = "课程信息编辑接口")
@RestController
public class CourseBaseInfoController {

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams) {

        CourseBase courseBase = new CourseBase();
        courseBase.setName(queryCourseParams.getCourseName());
        courseBase.setAuditStatus(queryCourseParams.getAuditStatus());
        courseBase.setCreateDate(LocalDateTime.now());

        ArrayList<CourseBase> courseBases = new ArrayList<>();
        courseBases.add(courseBase);

        PageResult pageResult = new PageResult(courseBases,
                10,
                pageParams.getPageNo(),
                pageParams.getPageSize());

        return pageResult;
    }

}
