package com.xuecheng.media.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {


    @Autowired
    MediaFileService mediaFileService;


    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiles(companyId, pageParams, queryMediaParamsDto);

    }

    @ApiOperation("课程媒体资源上传")
    @PostMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile fileToUpload,
                                      @RequestParam(value = "folder",required=false) String folder,
                                      @RequestParam(value = "objectName",required=false) String objectName) throws IOException {
        //todo:机构id
        Long companyId = 1234L;

        //构造uploadFileParamsDto
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(fileToUpload.getOriginalFilename());
        uploadFileParamsDto.setFileSize(fileToUpload.getSize());
        uploadFileParamsDto.setFileType("001001");
        //uploadFileParamsDto.setUsername();
        //uploadFileParamsDto.setTags();
        //uploadFileParamsDto.setRemark();

        //在本地创建临时文件
        File tempFile = File.createTempFile("minio", "temp");
        //将接收到的还在内存中的文件拷贝到临时文件中，以便获取文件路径供后续上传文件系统使用
        fileToUpload.transferTo(tempFile);
        //获取文件路径
        String localFilePath = tempFile.getAbsolutePath();

        return mediaFileService.uploadFile(companyId, uploadFileParamsDto, localFilePath, objectName);
    }



}
