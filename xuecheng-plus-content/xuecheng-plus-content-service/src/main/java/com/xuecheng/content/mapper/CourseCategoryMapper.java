package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.po.CourseCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author arvin
 * @since 2023-03-20 12:27:59
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

}
