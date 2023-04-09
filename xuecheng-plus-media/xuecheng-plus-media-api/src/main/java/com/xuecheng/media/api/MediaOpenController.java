package com.xuecheng.media.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
@RequestMapping("/open")
public class MediaOpenController {

    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation("文件预览")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId) {

        MediaFiles mediaFiles = mediaFileService.getMediaFilesById(mediaId);
        if (mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())) {
            return RestResponse.validfail("视频还未处理好，请再等等");
        }

        return RestResponse.success(mediaFiles.getUrl());
    }
}
