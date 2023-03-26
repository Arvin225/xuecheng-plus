package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    public void saveTeachplan(TeachplanDto teachplanDto) {
        //获取id，判断有无，有则更新，无则插入
        Long id = teachplanDto.getId();

        if (id == null) {
            //为新数据设置排序值
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            teachplanDto.setOrderby(count + 1);

            //插入
            int insert = teachplanMapper.insert(teachplanDto);
            if (insert <= 0) {
                XueChengPlusException.cast("课程章节或小节添加失败");
            }
        } else {
            //防小人的健壮判断
            Teachplan teachplan = teachplanMapper.selectById(id);
            if (teachplan == null) {
                XueChengPlusException.cast("无该章节或小节，无法更新，请使用添加功能");
            }
            //更新
            int i = teachplanMapper.updateById(teachplanDto);
            if (i <= 0) {
                XueChengPlusException.cast("课程章节或小节修改失败");
            }
        }

    }

    @Override
    public Integer deleteById(Long id) {
        //判空
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) {
            XueChengPlusException.cast("无该课程计划");
        }

        //判断有无子节点，无则删除
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        //查询子节点数（所有父节点id为当前节点id的个数）
        queryWrapper.eq(Teachplan::getParentid, teachplan.getId());
        Integer count1 = teachplanMapper.selectCount(queryWrapper);
        //有则抛异常
        if (count1 > 0) {
            XueChengPlusException.cast("该章节或小节下包含若干小节，无法直接删除");
        }
        //无则删除
        int del = teachplanMapper.deleteById(id);
        if (del < 1) {
            XueChengPlusException.cast("删除失败");
        }

        //同时删除teachplan_media
        LambdaQueryWrapper<TeachplanMedia> teachplanMediaQueryWrapper = new LambdaQueryWrapper<>();
        //查询记录数
        teachplanMediaQueryWrapper.eq(TeachplanMedia::getTeachplanId, teachplan.getId());
        Integer count2 = teachplanMediaMapper.selectCount(teachplanMediaQueryWrapper);
        //有就删
        if (count2 > 0) {
            int i = teachplanMediaMapper.delete(teachplanMediaQueryWrapper);
            //删除的数目不对，报异常
            if (i != count2) {
                XueChengPlusException.cast("删除失败");
            }
        }
        return del;
    }

    private int getTeachplanCount(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentid);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;

    }
}
