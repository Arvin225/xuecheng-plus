package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.*;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Api(value = "课程信息编辑接口", tags = "课程信息编辑接口")
@RestController
public class CourseBaseInfoController {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    CoursePublishService coursePublishService;

    @ApiOperation("课程查询接口")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams) {

        Long companyId = Long.valueOf(Objects.requireNonNull(SecurityUtil.getUser()).getCompanyId());

        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(companyId, pageParams, queryCourseParams);

        return pageResult;
    }

    @ApiOperation("课程新增接口")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Insert.class) AddCourseDto addCourseDto) {

        Long companyId = Long.valueOf(Objects.requireNonNull(SecurityUtil.getUser()).getCompanyId());

        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.createCourseBase(companyId, addCourseDto);

        return courseBaseInfoDto;
    }

    @ApiOperation("根据id查询课程的接口")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId) {
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("修改课程基本信息的接口")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody EditCourseDto editCourseDto) {

        Long companyId = Long.valueOf(Objects.requireNonNull(SecurityUtil.getUser()).getCompanyId());

        return courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
    }

    @ApiOperation("根据id删除课程的接口")
    @DeleteMapping("/course/{courseId}")
    public void deleteCourseBaseById(@PathVariable Long courseId) {
        courseBaseInfoService.deleteById(courseId);
    }

    @ApiOperation("查询课程全部信息")
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId){
        return coursePublishService.getCoursePreviewInfo(courseId);
    }



}
