package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

public interface CoursePublishService {

    /**
     * 获取课程预览数据
     * @param courseId 课程id
     * @return 课程预览数据模型
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * @description 提交审核
     * @param courseId  课程id
     * @return void
     * @author Mr.M
     * @date 2022/9/18 10:31
     */
    public void commitAudit(Long companyId,Long courseId);

}
