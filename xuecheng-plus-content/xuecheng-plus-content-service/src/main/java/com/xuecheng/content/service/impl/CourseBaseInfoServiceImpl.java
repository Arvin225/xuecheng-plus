package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseTeacherService courseTeacherService;
    @Autowired
    TeachplanService teachplanService;

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

    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
       /*
        //数据校验
        if (StringUtils.isBlank(dto.getName())) {
            throw new XueChengPlusException("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            throw new XueChengPlusException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            throw new XueChengPlusException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            throw new XueChengPlusException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new XueChengPlusException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            throw new XueChengPlusException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            throw new XueChengPlusException("收费规则为空");
        }*/

        //1.插入课程基本信息到课程基本信息表
        //1.1.数据表对应的数据模型创建
        CourseBase courseBase = new CourseBase();

        //1.2.将传递的数据对象中课程基本信息部分拷贝到数据模型中
        BeanUtils.copyProperties(dto, courseBase);

        //1.3.其他数据的补充
        courseBase.setCompanyId(companyId);//机构ID
        courseBase.setCreateDate(LocalDateTime.now());//创建时间
        //todo 创建人补充
        courseBase.setAuditStatus("202002");//审核状态：未提交
        courseBase.setStatus("203001");//发布状态：未发布

        //1.4.执行插入操作
        int insert = courseBaseMapper.insert(courseBase);
        //插入失败则抛出异常不往下执行
        if (insert <= 0) {
            throw new XueChengPlusException("课程基本信息插入失败");
        }


        //2.插入课程营销信息到课程营销信息表
        //2.1.数据表对应的数据模型创建
        CourseMarket courseMarket = new CourseMarket();
        //2.2.将传递的数据对象中课程营销信息部分拷贝到数据模型中
        BeanUtils.copyProperties(dto, courseMarket);
        //2.3.获取课程基本信息的id，营销信息的id与其保持一致
        courseMarket.setId(courseBase.getId());
        //2.4.执行插入操作
        int i = saveCourseMarket(courseMarket);
        if (i <= 0) {
            throw new XueChengPlusException("课程营销信息插入失败");
        }


        //3.结果返回

        /*
        //3.1.数据模型构造
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        //3.1.1.拷贝基本信息、营销信息到数据模型
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        //3.2.返回
        return courseBaseInfoDto;
        */

        //3.1.根据id查询基本信息及营销信息
        CourseBaseInfoDto courseBaseInfoDto = getCourseBaseInfo(courseBase.getId());
        //3.2.返回
        return courseBaseInfoDto;
    }

    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        //判空
        if (courseId == null) {
            throw new XueChengPlusException("课程id为空，无法继续操作");
        }

        //创建数据模型
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();

        //1.查询基本信息表、营销信息表
        //1.1.查询基本信息表
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //判空
        if (courseBase == null) {
            throw new XueChengPlusException("未查到该课程的基本信息，程序终止");
        }
        //拷贝基本信息到数据模型
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);

        //1.2.查询营销信息表
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //判空
        if (courseMarket != null) {
            //throw new XueChengPlusException("未查到该课程的营销信息，程序终止");
            //拷贝营销信息到数据模型
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        //2.构造返回数据模型
        return courseBaseInfoDto;
    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        //健壮性判断
        CourseBase courseBase = courseBaseMapper.selectById(editCourseDto.getId());
        if (courseBase == null) {
            XueChengPlusException.cast("未找到到该课程，您可去添加");
        }

        //更新course_base表
        BeanUtils.copyProperties(editCourseDto, courseBase);
        courseBase.setCompanyId(companyId);

        int i1 = courseBaseMapper.updateById(courseBase);
        if (i1 <= 0) {
            XueChengPlusException.cast("课程基本信息保存失败");
        }


        //更新course_market表
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, courseMarket);

        int i2 = saveCourseMarket(courseMarket);
        if (i2 <= 0) {
            XueChengPlusException.cast("课程营销信息保存失败");
        }

        return getCourseBaseInfo(editCourseDto.getId());
    }

    @Override
    public void deleteById(Long courseId) {
        /*
            删除逻辑
                已提交才能删
                删
                    课程师资
                    课程计划
                    课程
         */
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("无该课程");
        }
        String auditStatus = courseBase.getAuditStatus();
        if (!StringUtils.equals(auditStatus, "202002")) {
            XueChengPlusException.cast("课程已提交无法直接删除");
        }

        //删除课程师资（不管有无直接执行删除操作）
        courseTeacherService.delete(courseId, null);

        //删除课程计划（不管有无直接执行删除操作）
        teachplanService.deleteByCourseId(courseId);

        //删除课程
        //删除课程营销信息（不管有无直接执行删除操作）
        courseMarketMapper.deleteById(courseId);

        //删除课程基本信息
        courseBaseMapper.deleteById(courseId);

    }

    private int saveCourseMarket(CourseMarket courseMarket) {
        //1.收费规则
        //1.1.收费规则判空
        String charge = courseMarket.getCharge();
        if (StringUtils.isBlank(charge)) {
            throw new XueChengPlusException("收费规则没有选择");
        }
        //1.2.收费规则为"收费"
        if (charge.equals("201001")) {
            if (courseMarket.getPrice() == null || courseMarket.getPrice().floatValue() <= 0) {
                throw new XueChengPlusException("课程为收费价格不能为空且必须大于0");
            }
        }

        //todo2.数据校验

        //3.查询数据库有无此数据，有则更新，无责插入
        if (courseMarket.getId() == null) {
            throw new XueChengPlusException("课程id为空，程序终止");
        }
        CourseMarket courseMarketBySelect = courseMarketMapper.selectById(courseMarket.getId());
        if (courseMarketBySelect == null) {
            return courseMarketMapper.insert(courseMarket);
        }

        return courseMarketMapper.updateById(courseMarket);
    }
}
