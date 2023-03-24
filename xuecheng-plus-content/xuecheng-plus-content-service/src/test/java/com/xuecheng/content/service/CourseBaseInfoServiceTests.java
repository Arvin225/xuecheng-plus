package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
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

    @Test
    void testCreateCourseBase(){
        Long companyId = 1234L;
        AddCourseDto addCourseDto = new AddCourseDto();
        addCourseDto.setName("学成在线项目课程");
        addCourseDto.setMt("实战项目");
        addCourseDto.setSt("Java企业级项目");
        addCourseDto.setPic("afsdfasd");
        addCourseDto.setTeachmode("200002");
        addCourseDto.setUsers("中级人员");
        addCourseDto.setTags("xiangmu");
        addCourseDto.setGrade("204001");
        addCourseDto.setCharge("201000");
        addCourseDto.setPrice(1000F);
        addCourseDto.setOriginalPrice(899F);
        addCourseDto.setQq("qq1234");
        addCourseDto.setWechat("wechat1234");
        addCourseDto.setPhone("phone1234");
        addCourseDto.setValidDays(365);

        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);

        System.out.println(courseBase);
    }

    @Test
    void testUpdateCourse(){
        Long companyId = 1234L;
        EditCourseDto editCourseDto = new EditCourseDto();
        editCourseDto.setId(86L);
        editCourseDto.setName("学成在线项目课程");
        editCourseDto.setMt("实战项目");
        editCourseDto.setSt("Java企业级项目");
        editCourseDto.setPic("afsdfasd");
        editCourseDto.setTeachmode("200002");
        editCourseDto.setUsers("中级人员");
        editCourseDto.setTags("xiangmu");
        editCourseDto.setGrade("204001");
        editCourseDto.setCharge("201000");
        editCourseDto.setPrice(1000F);
        editCourseDto.setOriginalPrice(899F);
        editCourseDto.setQq("qq1234");
        editCourseDto.setWechat("wechat1234");
        editCourseDto.setPhone("phone1234");
        editCourseDto.setValidDays(365);
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
        System.out.println(courseBaseInfoDto);
    }



}
