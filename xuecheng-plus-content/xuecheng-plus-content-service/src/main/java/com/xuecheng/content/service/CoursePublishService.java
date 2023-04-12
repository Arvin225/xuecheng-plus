package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

public interface CoursePublishService {

    /**
     * 获取课程预览数据
     *
     * @param courseId 课程id
     * @return 课程预览数据模型
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * @param courseId 课程id
     * @return void
     * @description 提交审核
     * @author Mr.M
     * @date 2022/9/18 10:31
     */
    @Transactional
    void commitAudit(Long companyId, Long courseId);

    /**
     * @param companyId 机构id
     * @param courseId  课程id
     * @return void
     * @description 课程发布接口
     * @author Mr.M
     * @date 2022/9/20 16:23
     */
    @Transactional
    void publish(Long companyId, Long courseId);

    /**
     * @param courseId 课程id
     * @return File 静态化文件
     * @description 课程静态化
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    File generateCourseHtml(Long courseId);

    /**
     * @param file 静态化文件
     * @return void
     * @description 上传课程静态化页面
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    void uploadCourseHtml(Long courseId, File file);


}
