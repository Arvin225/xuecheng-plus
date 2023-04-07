package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author Mr.M
     * @date 2022/9/10 8:57
     */
    PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     *
     * @param companyId           机构id
     * @param uploadFileParamsDto 待上传文件的一些信息，如文件名
     * @param localFilePath       文件当前路径
     * @return 文件信息
     */
    //@Transactional
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);

    /**
     * 添加文件信息到数据库
     *
     * @param companyId           机构id
     * @param fileMd5             文件的md5
     * @param uploadFileParamsDto 上传文件需要的参数
     * @param bucket              桶
     * @param objectName          对象名
     * @return 文件信息
     */
    @Transactional
    MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

    /**
     * 上传文件前检查是否已上传，避免重复上传
     *
     * @param fileMd5 文件的md5值
     * @return 状态及信息
     */
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 上传分块前，判断分块是否已上传
     *
     * @param fileMd5    文件的md5值，用于解析存储路径
     * @param chunkIndex 序号作为分块名称
     * @return 状态及信息
     */
    RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * 上传分块
     *
     * @param fileMd5            文件md5值
     * @param chunk              序号作为分块名称
     * @param localChunkFilePath 保存在本地的分块路径
     * @return 状态及信息
     */
    RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath);

    /**
     * 合并分块
     * @param companyId 机构id
     * @param fileMd5 文件的md5值
     * @param chunkTotal 分块总数
     * @param uploadFileParamsDto 上传文件所需参数
     * @return 状态与信息
     */
    RestResponse mergeChunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);

    /**
     * 从minio下载文件
     *
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    File downloadFileFromMinIO(String bucket, String objectName);

    /**
     * @param localFilePath 文件地址
     * @param bucket        桶
     * @param objectName    对象名称
     * @return void
     * @description 将文件写入minIO
     * @author Mr.M
     * @date 2022/10/12 21:22
     */
    boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName);


}
