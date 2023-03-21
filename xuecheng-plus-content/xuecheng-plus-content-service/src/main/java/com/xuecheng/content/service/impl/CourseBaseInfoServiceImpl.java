package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParams) {

        //查询条件构造
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParams.getCourseName())//根据名称模糊查询
                        , CourseBase::getName
                        , queryCourseParams.getCourseName())
                //根据审核状态
                .eq(StringUtils.isNotEmpty(queryCourseParams.getAuditStatus())
                        , CourseBase::getAuditStatus
                        , queryCourseParams.getAuditStatus())
                //根据发布状态
                .eq(StringUtils.isNotEmpty(queryCourseParams.getPublishStatus())
                        , CourseBase::getStatus
                        , queryCourseParams.getPublishStatus());

        //分页查询
        Page<CourseBase> pageInfo = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        pageInfo = courseBaseMapper.selectPage(pageInfo, queryWrapper);

        //响应结果
        PageResult<CourseBase> pageResult = new PageResult<>(pageInfo.getRecords(), pageInfo.getTotal(), pageInfo.getCurrent(), pageInfo.getSize());
        return pageResult;
    }
}
