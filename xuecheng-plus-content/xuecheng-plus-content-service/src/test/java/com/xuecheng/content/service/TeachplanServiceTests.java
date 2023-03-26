package com.xuecheng.content.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TeachplanServiceTests {

    @Autowired
    TeachplanService teachplanService;

    @Test
    void saveTeachplan(){
        //teachplanService.saveTeachplan();
    }

    @Test
    void testDeleteTeachplan(){
        Integer delete = teachplanService.deleteById(268L);
        System.out.println(delete);
    }
}
