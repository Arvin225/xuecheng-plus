package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> getCourseTeachers(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Override
    public void saveCourseTeacher(CourseTeacher courseTeacher) {
        Long id = courseTeacher.getId();
        //有id则更新，无则插入
        if (id != null && courseTeacherMapper.selectById(id) != null) {
            //更新
            int update = courseTeacherMapper.updateById(courseTeacher);
            if (update < 1) {
                XueChengPlusException.cast("更新失败");
            }
            return;
        }
        //插入
        int insert = courseTeacherMapper.insert(courseTeacher);
        if (insert < 1) {
            XueChengPlusException.cast("添加失败");
        }
    }

    @Override
    public void delete(Long courseId, Long id) {
        if (courseId == null || id == null) {
            XueChengPlusException.cast("课程id或教师id不能为空");
        }
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId)
                .eq(CourseTeacher::getId, id);
        int delete = courseTeacherMapper.delete(queryWrapper);
        if (delete < 1) {
            XueChengPlusException.cast("删除失败");
        }
    }
}
