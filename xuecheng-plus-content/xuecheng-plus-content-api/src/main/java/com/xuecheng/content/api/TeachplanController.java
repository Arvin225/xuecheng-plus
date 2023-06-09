package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
@RestController
public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId", name = "课程Id", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {
        return teachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("添加/修改课程计划")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody TeachplanDto teachplanDto) {
        teachplanService.saveTeachplan(teachplanDto);
    }

    @ApiOperation("删除课程计划")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable Long id){
        teachplanService.deleteById(id);
    }

    @ApiOperation("上移")
    @PostMapping("/teachplan/moveup/{id}")
    public void moveup(@PathVariable Long id){
        teachplanService.move(true, id);
    }

    @ApiOperation("下移")
    @PostMapping("/teachplan/movedown/{id}")
    public void movedown(@PathVariable Long id){
        teachplanService.move(false, id);
    }

    @ApiOperation("添加媒资")
    @PostMapping("/teachplan/association/media")
    public void associationWithMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.bindMedia(bindTeachplanMediaDto);
    }

    @ApiOperation("删除媒资")
    @DeleteMapping("/teachplan/association/media/{teachplanId}/{mediaId}")
    public void deleteMedia(@PathVariable Long teachplanId, @PathVariable String mediaId){
        teachplanService.deleteMedia(teachplanId, mediaId);
    }


}
