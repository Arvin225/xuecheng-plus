package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.model.dto.CourseIndex;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    SearchServiceClient searchServiceClient;

    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() {
        //分片参数
        int shardTotal = XxlJobHelper.getShardTotal();
        int shardIndex = XxlJobHelper.getShardIndex();

        log.debug("shardIndex=" + shardIndex + ",shardTotal=" + shardTotal);

        //核心数
        int processors = Runtime.getRuntime().availableProcessors();

        //执行
        process(shardIndex, shardTotal, "course_publish", processors, 60);
    }

    @Override
    public boolean execute(MqMessage mqMessage) {
        //获取消息相关的业务信息
        String businessKey1 = mqMessage.getBusinessKey1();
        long courseId = Integer.parseInt(businessKey1);

        //课程静态化
        generateCourseHtml(mqMessage, courseId);
        //课程索引
        saveCourseIndex(mqMessage, courseId);
        //课程缓存
        saveCourseCache(mqMessage, courseId);

        return true;
    }

    /**
     * 生成课程静态化页面并上传至文件系统
     *
     * @param mqMessage mm
     * @param courseId  ci
     */
    private void generateCourseHtml(MqMessage mqMessage, long courseId) {
        log.debug("开始进行课程静态化,课程id:{}", courseId);
        //获取消息id
        Long id = mqMessage.getId();
        //获取消息处理的service
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息幂等性处理
        int stageOne = mqMessageService.getStageOne(id);
        if (stageOne > 0) {
            log.debug("课程静态化已处理直接返回，课程id:{}", courseId);
            return;
        }


        //页面静态化
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file == null) {
            XueChengPlusException.cast("页面静态化失败");
        }

        //上传minio
        coursePublishService.uploadCourseHtml(courseId, file);


        //休眠10秒
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //保存第一阶段状态（实现幂等性）
        mqMessageService.completedStageOne(id);
    }

    /**
     * 保存课程索引信息
     *
     * @param mqMessage mm
     * @param courseId  ci
     */
    private void saveCourseIndex(MqMessage mqMessage, long courseId) {
        log.debug("保存课程索引信息,课程id:{}", courseId);

        //消息id
        Long id = mqMessage.getId();
        //消息处理的service
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息幂等性处理
        int stageTwo = mqMessageService.getStageTwo(id);
        if (stageTwo > 0) {
            log.debug("课程索引已处理直接返回，课程id:{}", courseId);
            return;
        }

        //获取已发布的课程数据
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        if (coursePublish == null) {
            XueChengPlusException.cast("发布表中未查询到该课程");
        }
        //CourseIndex
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);

        //添加课程索引
        Boolean added = searchServiceClient.add(courseIndex);
        if (!added) {
            XueChengPlusException.cast("远程调用搜索服务添加课程索引失败");
        }

        mqMessageService.completedStageTwo(id);

        //休眠2秒
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将课程信息缓存至redis
     *
     * @param mqMessage mm
     * @param courseId  ci
     */
    private void saveCourseCache(MqMessage mqMessage, long courseId) {
        log.debug("将课程信息缓存至redis,课程id:{}", courseId);

        //......

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
