package com.example.mpgenerator.serviceImpl;

import com.example.mpgenerator.po.CourseCategory;
import com.example.mpgenerator.mapper.CourseCategoryMapper;
import com.example.mpgenerator.service.ICourseCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:50:46
 */
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements ICourseCategoryService {

}
