package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class MyCourseTablesServiceImpl implements MyCourseTablesService {
    @Autowired
    XcChooseCourseMapper chooseCourseMapper;

    @Autowired
    XcCourseTablesMapper courseTablesMapper;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    MyCourseTablesService myCourseTablesService;

    @Autowired
    MyCourseTablesServiceImpl currentProxy;

    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {
        if (StringUtils.isBlank(userId) || courseId == null) {
            log.error("用户id或课程id为空");
            XueChengPlusException.cast("选课失败");
        }
        /*
            选课逻辑
                查询课程发布表，判断收费情况
                    免费：添加到选课记录表，添加到课表
                    收费：添加到选课记录表
                查询学习资格
                    返回
         */

        //查询课程信息
        CoursePublish coursePublish = contentServiceClient.getCoursePublish(courseId);
        if (coursePublish == null) {
            log.error("课程发布表中未查到该选课，课程id：{}，用户id：{}", courseId, userId);
            XueChengPlusException.cast("选课失败");
        }

        //课程收费标准
        String charge = coursePublish.getCharge();

        //选课记录
        XcChooseCourse chooseCourse;

        if ("201000".equals(charge)) {//课程免费
            //添加免费课程
            chooseCourse = addFreeCourse(userId, coursePublish);
            //添加到我的课程表
            addCourseTables(chooseCourse);
        } else {
            //添加收费课程
            chooseCourse = addChargeCourse(userId, coursePublish);
        }

        XcChooseCourseDto chooseCourseDto = new XcChooseCourseDto();
        BeanUtils.copyProperties(chooseCourse, chooseCourseDto);

        //获取学习资格
        XcCourseTablesDto courseTablesDto = getLearningStatus(userId, courseId);
        String learnStatus = courseTablesDto.getLearnStatus();
        chooseCourseDto.setLearnStatus(learnStatus);

        //支付成功，更新选课状态为“成功”，设置课程生效时间、过期时间，并添加到课程表（监听支付成功的方法中编写）


        return chooseCourseDto;
    }

    /**
     * @param userId
     * @param courseId
     * @return XcCourseTablesDto 学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     * @description 判断学习资格
     * @author Mr.M
     * @date 2022/10/3 7:37
     */
    @Override
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId) {
        //返回值
        XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();

        //查询我的课程表
        XcCourseTables xcCourseTables = getXcCourseTables(userId, courseId);
        //没有选课或选课后没有支付，设置状态为702002，返回
        if (xcCourseTables == null) {
            xcCourseTablesDto.setLearnStatus("702002");
            return xcCourseTablesDto;
        }

        BeanUtils.copyProperties(xcCourseTables, xcCourseTablesDto);
        //是否过期,true过期，false未过期
        boolean isExpires = xcCourseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if (!isExpires) {
            //正常学习
            xcCourseTablesDto.setLearnStatus("702001");
        } else {
            //已过期
            xcCourseTablesDto.setLearnStatus("702003");
        }
        return xcCourseTablesDto;
    }

    @Override
    public boolean saveChooseCourseStatus(String chooseCourseId) {
        if (StringUtils.isBlank(chooseCourseId)) {
            log.error("选课id为空");
            return false;
        }

        XcChooseCourse chooseCourse = chooseCourseMapper.selectById(chooseCourseId);
        if (chooseCourse == null) {
            log.debug("课程支付成功，但选课状态修改失败，因为选课记录为空，选课id：{}", chooseCourseId);
            return false;
        }

        //非选课成功修改选课状态
        if (!"701001".equals(chooseCourse.getStatus())) {
            //修改选课状态为选课成功
            chooseCourse.setStatus("701001");
            int i = chooseCourseMapper.updateById(chooseCourse);
            if (i < 1) {
                log.debug("课程支付成功，但选课状态修改失败，选课id：{}，选课详情：{}", chooseCourseId, chooseCourse);
                return false;
            }
        }

        //添加到课程表
        addCourseTables(chooseCourse);

        return true;
    }

    @Override
    public PageResult<XcCourseTables> myCourseTables(MyCourseTableParams params) {

        //页码
        long pageNo = params.getPage();
        //每页记录数,固定为4
        long pageSize = 4;
        //分页条件
        Page<XcCourseTables> page = new Page<>(pageNo, pageSize);
        //根据用户id查询
        String userId = params.getUserId();
        LambdaQueryWrapper<XcCourseTables> lambdaQueryWrapper = new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getUserId, userId);

        //分页查询
        Page<XcCourseTables> pageResult = courseTablesMapper.selectPage(page, lambdaQueryWrapper);
        List<XcCourseTables> records = pageResult.getRecords();
        //记录总数
        long total = pageResult.getTotal();
        PageResult<XcCourseTables> courseTablesResult = new PageResult<>(records, total, pageNo, pageSize);
        return courseTablesResult;

    }

    /**
     * 查询课程表某课程
     *
     * @param userId
     * @param courseId
     * @return
     */
    private XcCourseTables getXcCourseTables(String userId, Long courseId) {

        LambdaQueryWrapper<XcCourseTables> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(XcCourseTables::getUserId, userId)
                .eq(XcCourseTables::getCourseId, courseId);

        XcCourseTables xcCourseTables = courseTablesMapper.selectOne(queryWrapper);
        return xcCourseTables;
    }

    /**
     * 添加免费课程到选课表
     *
     * @param userId
     * @param coursePublish
     * @return
     */
    private XcChooseCourse addFreeCourse(String userId, CoursePublish coursePublish) {
        if (StringUtils.isBlank(userId) || coursePublish == null) {
            log.error("添加免费课程到选课记录表失败，用户id为空或所选课程未发布");
            XueChengPlusException.cast("选课失败");
        }

        //添加到选课记录表
        //判断是否已选该课程，已选则不再继续执行
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(XcChooseCourse::getUserId, userId)//用户id
                .eq(XcChooseCourse::getCourseId, coursePublish.getId())//课程id
                .eq(XcChooseCourse::getOrderType, "700001")//选课类型为“免费”
                .eq(XcChooseCourse::getStatus, "701001")//选课状态为“成功”
                .gt(XcChooseCourse::getValidtimeEnd, LocalDateTime.now());//课程未过期
        //这里不用selectOne()是因为业务表没有唯一约束，可能出现重复记录，强行使用的话会报异常
        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(queryWrapper);
        //表中已有记录则直接返回
        if (xcChooseCourses != null && xcChooseCourses.size() > 0) {
            log.debug("重复添加，选课表中已有该课程，课程id：{}，用户id：{}", coursePublish.getId(), userId);
            return xcChooseCourses.get(0);
        }

        //无则构造数据模型插入数据表
        XcChooseCourse chooseCourse = new XcChooseCourse();
        //数据封装
        chooseCourse.setCourseId(coursePublish.getId());//课程id
        chooseCourse.setCourseName(coursePublish.getName());
        chooseCourse.setUserId(userId);//用户id
        chooseCourse.setCompanyId(coursePublish.getCompanyId());
        chooseCourse.setOrderType("700001");//选课类型：[{"code":"700001","desc":"免费课程"},{"code":"700002","desc":"收费课程"}]
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(0F);//免费课程价格为0
        chooseCourse.setValidDays(365);//课程有效期，免费课程默认365天
        chooseCourse.setStatus("701001");//选课状态：[{"code":"701001","desc":"选课成功"},{"code":"701002","desc":"待支付"}]
        chooseCourse.setValidtimeStart(LocalDateTime.now());//课程生效日期
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));//课程过期时间，生效时间加上有效期

        //插入
        int insert = chooseCourseMapper.insert(chooseCourse);
        if (insert < 1) {
            log.error("添加免费课程到选课记录表失败，课程id：{}，用户id：{}", chooseCourse.getCourseId(), userId);
            XueChengPlusException.cast("选课失败");
        }

        return chooseCourse;
    }

    /**
     * 添加课程到课程表
     *
     * @param chooseCourse
     * @return
     */
    private XcCourseTables addCourseTables(XcChooseCourse chooseCourse) {
        if (chooseCourse == null) {
            log.error("添加选课到课程表失败，该选课记录为空");
            XueChengPlusException.cast("选课失败");
        }

        /*
        判断是否成功选课：
            查询课程表中是否已有该课程
            添加课程
         */

        //未成功选课，抛出异常
        if (!"701001".equals(chooseCourse.getStatus())) {
            log.error("添加选课到课程表失败，未成功选课，课程id：{}，用户id：{}", chooseCourse.getCourseId(), chooseCourse.getUserId());
            XueChengPlusException.cast("选课失败");
        }

        //查询课程表是否已有该课程
        LambdaQueryWrapper<XcCourseTables> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XcCourseTables::getUserId, chooseCourse.getUserId())
                .eq(XcCourseTables::getCourseId, chooseCourse.getCourseId());
        //这里直接使用selectOne()，因为对应业务表中有唯一约束，不会出现重复记录
        XcCourseTables courseTables = courseTablesMapper.selectOne(queryWrapper);
        //已有课程直接返回
        if (courseTables != null) {
            return courseTables;
        }

        //无则创建数据模型插入数据表
        courseTables = new XcCourseTables();
        //数据封装
        //拷贝能拷贝的
        BeanUtils.copyProperties(chooseCourse, courseTables);
        //封装没拷贝的
        courseTables.setChooseCourseId(chooseCourse.getId());//选课id
        courseTables.setCourseType(chooseCourse.getOrderType());//课程类型，即选课类型：收费/免费
        courseTables.setUpdateDate(LocalDateTime.now());//更新时间

        int insert = courseTablesMapper.insert(courseTables);
        if (insert < 1) {
            log.error("添加课程到课程表失败，选课id：{}，用户id：{}", courseTables.getChooseCourseId(), courseTables.getUserId());
            XueChengPlusException.cast("选课失败");
        }

        return courseTables;
    }

    /**
     * 添加收费课程到选课表
     *
     * @param userId
     * @param coursePublish
     * @return
     */
    private XcChooseCourse addChargeCourse(String userId, CoursePublish coursePublish) {
        //添加到选课记录表
        if (StringUtils.isBlank(userId) || coursePublish == null) {
            log.error("添加收费课程到选课记录表失败，用户id为空或所选课程未发布");
            XueChengPlusException.cast("选课失败");
        }

        //添加到选课记录表
        //判断是否已选该课程，已选则不再继续执行
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(XcChooseCourse::getUserId, userId)//用户id
                .eq(XcChooseCourse::getCourseId, coursePublish.getId())//课程id
                .eq(XcChooseCourse::getOrderType, "700002")//选课类型为“付费”
                .eq(XcChooseCourse::getStatus, "701002");//选课状态为“待支付”
        //这里不用selectOne()是因为业务表没有唯一约束，可能出现重复记录，强行使用的话会报异常
        List<XcChooseCourse> chooseCourses = chooseCourseMapper.selectList(queryWrapper);
        //表中已有记录则直接返回
        if (chooseCourses != null && chooseCourses.size() > 0) {
            log.debug("重复添加，选课表中已有该课程，课程id：{}，用户id：{}", coursePublish.getId(), userId);
            return chooseCourses.get(0);
        }

        //无则构造数据模型插入数据表
        XcChooseCourse chooseCourse = new XcChooseCourse();
        //数据封装
        chooseCourse.setCourseId(coursePublish.getId());//课程id
        chooseCourse.setCourseName(coursePublish.getName());
        chooseCourse.setUserId(userId);//用户id
        chooseCourse.setCompanyId(coursePublish.getCompanyId());
        chooseCourse.setOrderType("700002");//选课类型：[{"code":"700001","desc":"免费课程"},{"code":"700002","desc":"收费课程"}]
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursePublish.getPrice());//课程价格
        chooseCourse.setStatus("701002");//选课状态：[{"code":"701001","desc":"选课成功"},{"code":"701002","desc":"待支付"}]

        chooseCourse.setValidDays(coursePublish.getValidDays());//课程有效期
        chooseCourse.setValidtimeStart(LocalDateTime.now());//生效时间
        chooseCourse.setValidtimeEnd(LocalDateTime.now()
                .plusDays(coursePublish.getValidDays()));//失效时间

        //插入
        int insert = chooseCourseMapper.insert(chooseCourse);
        if (insert < 1) {
            log.error("添加收费课程到选课记录表失败，课程id：{}，用户id：{}", chooseCourse.getCourseId(), userId);
            XueChengPlusException.cast("选课失败");
        }

        return chooseCourse;
    }

}
