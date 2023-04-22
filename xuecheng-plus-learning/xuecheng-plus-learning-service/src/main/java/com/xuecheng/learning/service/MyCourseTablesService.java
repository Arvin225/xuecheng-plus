package com.xuecheng.learning.service;

import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import org.springframework.transaction.annotation.Transactional;

public interface MyCourseTablesService {
    /**
     * @description 添加选课
     * @param userId 用户id
     * @param courseId 课程id
     * @return com.xuecheng.learning.model.dto.XcChooseCourseDto
     * @author Mr.M
     * @date 2022/10/24 17:33
     */
    @Transactional
    XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * @description 判断学习资格
     * @param userId 用户id
     * @param courseId 课程id
     * @return XcCourseTablesDto 学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     * @author Mr.M
     * @date 2022/10/3 7:37
     */
    XcCourseTablesDto getLearningStatus(String userId, Long courseId);

    /**
     * 更改选课状态为成功，并添加到课表
     * @param chooseCourseId 选课id
     * @return 最终是否成功
     */
    @Transactional
    boolean saveChooseCourseStatus(String chooseCourseId);
}
