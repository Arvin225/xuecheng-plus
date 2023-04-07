package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeachplanService {

    /**
     * 查询课程计划
     * @param courseId 课程id
     * @return 课程计划树
     */
    List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 添加/修改课程计划
     * @param teachplanDto 课程计划dto
     */
    @Transactional
    void saveTeachplan(TeachplanDto teachplanDto);

    /**
     * 删除单个课程计划
     * @param id 课程计划id
     */
    @Transactional
    Integer deleteById(Long id);

    /**
     * 删除课程的所有计划
     * @param courseId 课程id
     * @return 删除成功的数目
     */
    @Transactional
    Integer deleteByCourseId(Long courseId);

    /**
     * 课程计划上移/下移展示
     * @param up 是否上移
     * @param id 课程计划id
     */
    @Transactional
    void move(Boolean up, Long id);

    /**
     * 绑定媒资（操作课程计划-媒资关系表）
     *
     * @param bindTeachplanMediaDto 参数
     * @return 课程计划的媒资信息
     */
    @Transactional
    TeachplanMedia bindMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /**
     * 删除媒资
     * @param teachplanId 课程计划id
     * @param mediaId 媒资id
     */
    @Transactional
    void deleteMedia(Long teachplanId, String mediaId);
}
