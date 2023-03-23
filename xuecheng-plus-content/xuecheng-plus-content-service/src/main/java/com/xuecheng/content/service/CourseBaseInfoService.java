package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.springframework.transaction.annotation.Transactional;

public interface CourseBaseInfoService {

    /**
     * 课程信息分页查询
     * @param pageParams 分页参数
     * @param queryCourseParams 查询参数
     * @return 分页查询结果
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParams);

    /**
     * 课程新增
     * @param addCourseDto 待添加的课程数据
     * @return 课程基本信息
     */
    @Transactional
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

}
