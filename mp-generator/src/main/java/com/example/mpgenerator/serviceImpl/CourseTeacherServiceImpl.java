package com.example.mpgenerator.serviceImpl;

import com.example.mpgenerator.po.CourseTeacher;
import com.example.mpgenerator.mapper.CourseTeacherMapper;
import com.example.mpgenerator.service.ICourseTeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:50:46
 */
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements ICourseTeacherService {

}
