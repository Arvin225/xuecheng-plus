package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CourseBaseInfoServiceTests {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Test
    void testQueryCourseBaseList(){
        //参数构造
        PageParams pageParams = new PageParams(1L, 2L);

        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("spring");
        queryCourseParamsDto.setAuditStatus("202004");
        queryCourseParamsDto.setPublishStatus("203002");

        //查询
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        System.out.println("courseBasePageResult = " + courseBasePageResult);
    }

}
