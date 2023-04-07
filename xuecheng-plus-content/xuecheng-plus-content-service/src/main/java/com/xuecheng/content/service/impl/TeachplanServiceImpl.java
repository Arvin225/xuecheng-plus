package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
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
        /*if (del < 1) {
            XueChengPlusException.cast("删除失败");
        }*///冗余

        //同时删除teachplan_media
        LambdaQueryWrapper<TeachplanMedia> teachplanMediaQueryWrapper = new LambdaQueryWrapper<>();
        //查询记录数
        teachplanMediaQueryWrapper.eq(TeachplanMedia::getTeachplanId, teachplan.getId());
        Integer count2 = teachplanMediaMapper.selectCount(teachplanMediaQueryWrapper);
        //有就删
        if (count2 > 0) {
            int i = teachplanMediaMapper.delete(teachplanMediaQueryWrapper);
            //删除的数目不对，报异常
            if (i != count2) {//todo：是否冗余有待商榷
                XueChengPlusException.cast("删除失败");
            }
        }
        return del;
    }

    @Override
    public Integer deleteByCourseId(Long courseId) {
        if (courseId == null) {
            XueChengPlusException.cast("课程id为空");
        }

        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);

        int delete = teachplanMapper.delete(queryWrapper);
        /*if (delete < 1) {
            XueChengPlusException.cast("该课程的计划删除失败");
        }*///冗余

        //删除媒资信息，有才删
        LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachplanMedia::getCourseId, courseId);

        List<TeachplanMedia> mediaList = teachplanMediaMapper.selectList(wrapper);
        if (!mediaList.isEmpty()) {
            teachplanMediaMapper.delete(wrapper);
            /*if (i < 1) {
                XueChengPlusException.cast("删除失败");
            }*///冗余
        }

        return delete;
    }

    @Override
    public void move(Boolean up, Long id) {
        //查询当前节点
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) {
            XueChengPlusException.cast("该课程计划不存在");
        }

        //查询同级别节点同时排序，返回的是list
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId())
                .eq(Teachplan::getParentid, teachplan.getParentid())
                .orderByAsc(Teachplan::getOrderby);//这里与查询课程计划树时的排序模式保持一致
        List<Teachplan> list = teachplanMapper.selectList(queryWrapper);

        //获取当前节点的位置，即索引
        //int index = list.indexOf(teachplan);
        //遍历获取
        int index = -1;//赋初值，也是未找到时的值
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(teachplan.getId())) {
                index = i;
                break;
            }
        }

        //特殊情况处理
        //若是第一个，则无法上移
        if (index == 0 && up) {
            XueChengPlusException.cast("已是最上层，无法继续上移");
        }
        //若是最后一个，则无法下移
        if (index == list.size() - 1 && !up) {
            XueChengPlusException.cast("已是最底层，无法继续下移");
        }

        //一般情况
        //取出上一个或下一个节点，并将其orderBy取出
        Teachplan lastOrNextNode = list.get(up ? index - 1 : index + 1);
        Integer nodeOrderby = lastOrNextNode.getOrderby();
        //获取当前节点的orderBy
        Integer orderby = teachplan.getOrderby();
        //交换
        teachplan.setOrderby(nodeOrderby);
        lastOrNextNode.setOrderby(orderby);
        //更新进数据表
        int update1 = teachplanMapper.updateById(teachplan);
        if (update1 < 1) {
            XueChengPlusException.cast(up ? "上移失败" : "下移失败");
        }
        int update2 = teachplanMapper.updateById(lastOrNextNode);
        if (update2 < 1) {
            XueChengPlusException.cast(up ? "上移失败" : "下移失败");
        }


    }

    @Override
    public TeachplanMedia bindMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {

        //获取课程计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        if (teachplanId == null) {
            XueChengPlusException.cast("课程计划id为空");
        }
        //获取课程计划
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);//查询课程计划获取课程id
        if (teachplan == null) {
            XueChengPlusException.cast("无该课程计划");
        }
        Integer grade = teachplan.getGrade();
        if (grade == 1) {
            XueChengPlusException.cast("只允许二级课程计划绑定媒资");
        }

        //删除当前课程计划已有的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getTeachplanId, teachplanId));

        //插入传入的媒资信息
        //封装
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCourseId(teachplan.getCourseId());

        //插入
        teachplanMediaMapper.insert(teachplanMedia);

        return teachplanMedia;
    }

    @Override
    public void deleteMedia(Long teachplanId, String mediaId) {
        if (teachplanId == null || mediaId == null) {
            XueChengPlusException.cast("课程计划id或媒资id为空");
        }
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId)
                .eq(TeachplanMedia::getMediaId, mediaId);
        teachplanMediaMapper.delete(queryWrapper);
    }

    private int getTeachplanCount(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentid);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;

    }
}
