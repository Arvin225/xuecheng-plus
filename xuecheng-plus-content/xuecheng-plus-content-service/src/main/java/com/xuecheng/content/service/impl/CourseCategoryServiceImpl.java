package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes() {

        LambdaQueryWrapper<CourseCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(CourseCategory::getOrderby);
        List<CourseCategory> courseCategoryList = courseCategoryMapper.selectList(queryWrapper);

        if (courseCategoryList.isEmpty()){
            return null;
        }

        ArrayList<CourseCategoryTreeDto> parentNodeList = new ArrayList<>();
        for (CourseCategory courseCategory : courseCategoryList) {

            CourseCategoryTreeDto courseCategoryTreeDto = new CourseCategoryTreeDto();
            BeanUtils.copyProperties(courseCategory, courseCategoryTreeDto);

            if (courseCategoryTreeDto.getIsLeaf() == 1) {

                if (!parentNodeList.isEmpty()) {
                    for (CourseCategoryTreeDto parentNode : parentNodeList) {

                        if (StringUtils.equals(courseCategoryTreeDto.getParentid(), parentNode.getId())) {
                            List<CourseCategoryTreeDto> list = parentNode.getChildrenTreeNodes();
                            list.add(courseCategoryTreeDto);
                            parentNode.setChildrenTreeNodes(list);
                        }

                    }
                }

            } else {
                /*if (parentNodeList.size() == 1){
                    CourseCategoryTreeDto courseCategoryTreeDto1 = parentNodeList.get(0);
                    //判断list里的节点与当前节点的父节点id是否相等，不相等则必有一个为根节点
                    if (!StringUtils.equals(
                            courseCategoryTreeDto.getParentid(),
                            courseCategoryTreeDto1.getParentid())) {
                        //判断list里的节点是否为根节点，是则将其移除，然后添加当前节点进list
                        if (StringUtils.equals(
                                courseCategoryTreeDto1.getId(),
                                courseCategoryTreeDto.getParentid())) {
                         parentNodeList.remove(0);
                         parentNodeList.add(courseCategoryTreeDto);
                        }
                        //不是则说明当前节点为根节点，不做任何操作
                    }else {

                    }

                }
                if (!"0".equals(courseCategoryTreeDto.getParentid())) {
                }*/
                ArrayList<CourseCategoryTreeDto> list = new ArrayList<>();
                courseCategoryTreeDto.setChildrenTreeNodes(list);
                parentNodeList.add(courseCategoryTreeDto);
            }

        }

        //删除根节点
        parentNodeList.removeIf(courseCategoryTreeDto -> courseCategoryTreeDto.getChildrenTreeNodes().isEmpty());

        return parentNodeList;
    }
}
