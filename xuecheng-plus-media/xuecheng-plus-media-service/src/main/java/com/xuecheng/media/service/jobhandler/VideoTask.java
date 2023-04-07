package com.xuecheng.media.service.jobhandler;

import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class VideoTask {

    @Autowired
    MediaFileProcessService mediaFileProcessService;

    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Autowired
    MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    String ffmpeg_path;

    @XxlJob("videoEncodeToMp4Handler")
    //@Transactional
    public void videoEncodeToMp4Handler() {
        int shardTotal = XxlJobHelper.getShardTotal();
        int shardIndex = XxlJobHelper.getShardIndex();

        //获取CPU核心数
        int count = Runtime.getRuntime().availableProcessors();

        //获取任务列表
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardTotal, shardIndex, count);
        if (mediaProcessList == null) {
            return;
        }

        //创建线程池，线程数为任务数
        ExecutorService threadPool = Executors.newFixedThreadPool(mediaProcessList.size());

        //还有任务执行时，确保主线程处于阻塞状态，所有任务执行完成后取消阻塞，用await()方法配合计数器实现
        //计数器，初始值为任务数，也即线程数
        CountDownLatch countDownLatch = new CountDownLatch(mediaProcessList.size());

        //执行转码，多线程执行，所有任务同时执行
        mediaProcessList.forEach(item -> {
            threadPool.execute(() -> {
                try {
                    //开始任务，将状态改为处理中，形成乐观锁
                    Boolean processing = mediaFileProcessService.setProcessing(item.getId());
                    if (!processing) {
                        return;
                    }

                    log.debug("开始执行任务:{}", item);

                    //下载源视频
                    File sourceFile = mediaFileService.downloadFileFromMinIO(item.getBucket(), item.getFilePath());
                    if (sourceFile == null) {
                        log.error("源视频下载出错，taskId:{}，bucket:{}，url:{}", item.getId(), item.getBucket(), item.getUrl());
                        mediaFileProcessService.saveProcessFinishStatus(item.getId(), "3", item.getFileId(), null, "源视频下载出错");
                        return;
                    }

                    File mp4File;
                    try {
                        //创建临时MP4文件
                        mp4File = File.createTempFile("mp4", ".mp4");
                    } catch (IOException e) {
                        log.error("创建mp4临时文件失败");
                        mediaFileProcessService.saveProcessFinishStatus(item.getId(), "3", item.getFileId(), null, "创建mp4临时文件失败");
                        return;
                    }

                    //使用自定义的Mp4VideoUtil工具类操作FFmpeg开始转码
                    Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path, sourceFile.getAbsolutePath(), mp4File.getName(), mp4File.getAbsolutePath());
                    String feedback = mp4VideoUtil.generateMp4();
                    //转码不成功
                    if (!StringUtils.equals(feedback, "success")) {
                        log.error("视频转码失败，taskId:{}，bucket:{}，url:{}，错误信息：{}", item.getId(), item.getBucket(), item.getUrl(), feedback);
                        mediaFileProcessService.saveProcessFinishStatus(item.getId(), "3", item.getFileId(), null, feedback);
                        return;
                    }
                    //转码成功
                    //上传到minio
                    String mimeType = ContentInfoUtil.findExtensionMatch(".mp4").getMimeType();
                    String objectName = getFilePathByMd5(item.getFileId(), ".mp4");
                    boolean toMinIO = mediaFileService.addMediaFilesToMinIO(mp4File.getAbsolutePath(), mimeType, item.getBucket(), objectName);
                    //上传失败
                    if (!toMinIO) {
                        log.error("视频转码后上传失败，taskId:{}", item.getId());
                        mediaFileProcessService.saveProcessFinishStatus(item.getId(), "3", item.getFileId(), null, "视频转码后上传失败");
                        return;
                    }
                    //上传成功
                    //保存
                    String url = "/" + item.getBucket() + "/" + objectName;//访问url
                    mediaFileProcessService.saveProcessFinishStatus(item.getId(), "2", item.getFileId(), url, null);

                } finally { //为避免任务执行异常而导致计数器不执行计数
                    //计数器减一
                    countDownLatch.countDown();
                }
            });
        });

        try {
            //这里当计数器为0时（即所有任务执行完毕），取消阻塞，除此之外设置最大执行时间，超时也取消阻塞
            countDownLatch.await(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

}


