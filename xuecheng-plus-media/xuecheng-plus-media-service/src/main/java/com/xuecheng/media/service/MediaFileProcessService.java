package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MediaFileProcessService {

    /**
     * 获取自己的任务
     * @param shardTotal 执行器实例数
     * @param shardIndex 当前执行器编号
     * @param count 一次获取的任务数
     * @return 任务列表
     */
    List<MediaProcess> getMediaProcessList(int shardTotal, int shardIndex, int count);

    /**
     * 获取到任务后修改状态为处理中，防止其他线程来操作（乐观锁）
     * @param id 任务id
     * @return 更新记录数
     */
    @Transactional
    Boolean setProcessing(long id);

    /**
     * 任务执行完保存相关信息
     * @param taskId 任务id
     * @param status 状态
     * @param fileId 文件id
     * @param url 转码后的文件的在文件系统中的路径
     * @param errorMsg 错误信息
     */
    @Transactional
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}
