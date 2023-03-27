package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachplanDto;
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
     * 删除课程计划
     * @param id 课程计划id
     */
    @Transactional
    Integer deleteById(Long id);

    /**
     * 课程计划上移/下移展示
     * @param up 是否上移
     * @param id 课程计划id
     */
    @Transactional
    void move(Boolean up, Long id);
}
