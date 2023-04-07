package com.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    /**
     * 只拿自己的任务
     * @param shardTotal 执行器实例数
     * @param shardIndex 当前实例编号
     * @param count 一次拿多少个
     * @return 任务列表
     */
    @Select("select * from media_process where (status=1 or status=3) and fail_count<3 and id%#{shardTotal}=#{shardIndex} limit #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal,
                                              @Param("shardIndex") int shardIndex,
                                              @Param("count") int count);

    /**
     * 获取到任务后修改状态为处理中，防止其他线程来操作（乐观锁）
     * @param id 任务id
     * @return 更新记录数
     */
    @Update("update media_process set status='4' where (status='1' or status='3') and fail_count<3 and id=#{id}")
    int updateStatusToProcessing(@Param("id") long id);

}
