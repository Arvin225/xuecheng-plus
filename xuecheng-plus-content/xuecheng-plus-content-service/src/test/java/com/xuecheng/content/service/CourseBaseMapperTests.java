package com.xuecheng.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CourseBaseMapperTests {

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Test
    void listTest() {
        //参数构造
        PageParams pageParams = new PageParams(2L, 1L);

        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("spring");
        queryCourseParamsDto.setAuditStatus("202004");

        //查询条件构造
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName())
                        , CourseBase::getName
                        , queryCourseParamsDto.getCourseName())
                .eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus())
                        , CourseBase::getAuditStatus
                        , queryCourseParamsDto.getAuditStatus());

        //分页查询
        Page<CourseBase> pageInfo = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        pageInfo = courseBaseMapper.selectPage(pageInfo, queryWrapper);

        //响应结果
        PageResult<CourseBase> pageResult = new PageResult<>(pageInfo.getRecords(), pageInfo.getTotal(), pageInfo.getCurrent(), pageInfo.getSize());
    }

}
