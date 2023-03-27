package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "师资管理接口", tags = "师资管理接口")
@RestController
public class CourseTeacherController {

    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("查询课程师资")
    @GetMapping("courseTeacher/list/{courseId}")
    public List<CourseTeacher> getCourseTeachers(@PathVariable Long courseId){
        return courseTeacherService.getCourseTeachers(courseId);
    }

    @ApiOperation("添加/编辑教师")
    @PostMapping("/courseTeacher")
    public void addCourseTeacher(@RequestBody CourseTeacher courseTeacher){
        courseTeacherService.saveCourseTeacher(courseTeacher);
    }

    @ApiOperation("删除教师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteCourseTeacher(@PathVariable("courseId") Long courseId, @PathVariable("id") Long id){
        courseTeacherService.delete(courseId, id);
    }

}
