package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CourseCategoryServiceTests {

    @Autowired
    CourseCategoryService courseCategoryService;

    @Test
    void testList(){
        List<CourseCategoryTreeDto> list = courseCategoryService.queryTreeNodes();
        System.out.println("list = " + list);
    }
}
