package com.xuecheng.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.po.CourseCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CourseCategoryMapperTests {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Test
    void test(){
        LambdaQueryWrapper<CourseCategory> queryWrapper = new LambdaQueryWrapper<>();
        List<CourseCategory> courseCategoryList = courseCategoryMapper.selectList(queryWrapper);
        System.out.println(courseCategoryList);
    }
}
