package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

public interface CoursePublishService {

    /**
     * 获取课程预览数据
     * @param courseId 课程id
     * @return 课程预览数据模型
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);
}
