package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 课程师资管理
 */
public interface CourseTeacherService {

    /**
     * 课程师资查询
     * @param courseId 课程id
     * @return 师资
     */
    List<CourseTeacher> getCourseTeachers(Long courseId);

    /**
     * 添加或编辑一位教师
     * @param courseTeacher
     */
    @Transactional
    void saveCourseTeacher(CourseTeacher courseTeacher);

    /**
     * 删除课程教师
     * @param courseId 课程id
     * @param id 教师id
     * @return 删除成功的数目
     */
    @Transactional
    Integer delete(Long courseId, Long id);
}
