package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MinioClient minioClient;

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MediaProcessMapper mediaProcessMapper;

    //普通文件桶
    @Value("${minio.bucket.files}")
    private String bucket_files;

    //视频文件桶
    @Value("${minio.bucket.videofiles}")
    private String bucket_video;

    @Autowired
    MediaFileService currentProxy;


    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName) {

        File file = new File(localFilePath);
        if (!file.exists()) {
            XueChengPlusException.cast("文件不存在");
        }

        //获取上传的文件名
        String filename = uploadFileParamsDto.getFilename();
        //获取文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));

        //获取文件的mimeType类型
        String mimeType = getMimeType(extension);
        //拼接文件上传后在minio上的对象名
        //String objectName = folderPath + filename + extension;
        if (StringUtils.isEmpty(objectName)) {
            //获取根据日期生成的路径作为上传文件的路径
            String folderPath = getDefaultFolderPath();
            objectName = folderPath + filename;
        }

        //上传到minio
        boolean addToMinIO = addMediaFilesToMinIO(localFilePath, mimeType, bucket_files, objectName);
        if (!addToMinIO) {
            XueChengPlusException.cast("文件上传失败");
        }

        //获取文件的md5值
        String fileMd5 = getFileMd5(file);
        //设置的大小
        uploadFileParamsDto.setFileSize(file.length());
        //文件信息存入数据库
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_files, objectName);
        if (mediaFiles == null) {
            XueChengPlusException.cast("文件上传失败");
        }

        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);

        return uploadFileResultDto;
    }

    /**
     * 获取文件默认存储目录路径 年/月/日
     *
     * @return 存储路径
     */
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/") + "/";
        return folder;
    }

    /**
     * 获取文件的md5值
     *
     * @param file 文件
     * @return md5值
     */
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取文件的mimeType
     *
     * @param extension 文件扩展名
     * @return mimetype
     */
    private String getMimeType(String extension) {
        if (extension == null)
            extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .filename(localFilePath)
                            .contentType(mimeType)
                            .build());
            log.debug("上传文件到minio成功,bucket:{},objectName:{}", bucket, objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}", bucket, objectName, e.getMessage(), e);
            XueChengPlusException.cast("上传文件到文件系统失败");
        }
        return false;
    }

    @Override
    public MediaFiles getMediaFilesById(String mediaId) {
        return mediaFilesMapper.selectById(mediaId);
    }

    /**
     * @param companyId           机构id
     * @param fileMd5             文件md5值
     * @param uploadFileParamsDto 上传文件的信息
     * @param bucket              桶
     * @param objectName          对象名称
     * @return com.xuecheng.media.model.po.MediaFiles
     * @description 将文件信息添加到文件表
     * @author Mr.M
     * @date 2022/10/12 21:22
     */
    //@Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                log.error("保存文件信息到数据库失败,{}", mediaFiles);
                XueChengPlusException.cast("保存文件信息失败");
            }

            //将文件信息添加到待处理任务表
            addWaitingTask(mediaFiles);

            log.debug("保存文件信息到数据库成功,{}", mediaFiles);
        }
        return mediaFiles;

    }

    private void addWaitingTask(MediaFiles mediaFiles) {
        //判断是否需要将当前上传的视频转成MP4格式，这里暂时只对avi做转码
        //通过mimeType判断
        String filename = mediaFiles.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        String mimeType = getMimeType(extension);
        //需要则将信息记录到待处理数据表
        if (StringUtils.equals(mimeType, "video/x-msvideo")) {
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles, mediaProcess);
            mediaProcess.setStatus("1");//未处理
            mediaProcess.setFailCount(0);//失败次数默认为0
            mediaProcessMapper.insert(mediaProcess);
        }
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {

        //检查文件信息表中有无该文件，因为minio的检查需要文件扩展名
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        //有，检查minio
        if (mediaFiles != null) {
            String filePath = mediaFiles.getFilePath();//文件路径
            String bucket = mediaFiles.getBucket();//桶
            //尝试获取
            try {
                minioClient.getObject(
                        GetObjectArgs
                                .builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build());
            } catch (Exception e) { //这里注意，minio sdk设计的是没查到直接抛ErrResponse异常，而不是返回空，因此直接在这里判空
                //minio无，返回false
                return RestResponse.success(false);
            }

            //minio有，返回true（还应检查md5是否一致）
            return RestResponse.success(true);


        }
        //无，返回false（若上传时的顺序是先存数据库，后上传文件系统，那么此逻辑行得通，否则数据库没有，文件系统不一定没有）
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {

        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);//获取分块的目录
        String objectName = chunkFileFolderPath + chunkIndex; //拼接成分块路径

        try {
            minioClient.getObject(
                    GetObjectArgs
                            .builder()
                            .bucket(bucket_video)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            //没有，返回false
            return RestResponse.success(false);
        }
        //有，返回true
        return RestResponse.success(true);


    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {
        String objectName = getChunkFileFolderPath(fileMd5) + chunk;//构建指定的文件路径
        String mimeType = getMimeType(null);//获取mimetype
        //调用添加到minio的方法
        boolean toMinIO = addMediaFilesToMinIO(localChunkFilePath, mimeType, bucket_video, objectName);
        //添加失败
        if (!toMinIO) {
            log.debug("分块上传失败，分块名：{}", objectName);
            return RestResponse.validfail("分块上传失败");
        }
        //添加成功
        log.debug("分块上传成功，分块名：{}", objectName);
        return RestResponse.success(true);
    }

    @Override
    public RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {

        /*----------------------------------------------分块文件的合并----------------------------------------------*/
        //生成合并后的文件的路径
        String filename = uploadFileParamsDto.getFilename();//获取文件名
        String extension = filename.substring(filename.lastIndexOf("."));//获取扩展名
        String objectName = getFilePathByMd5(fileMd5, extension);//生成路径

        //构造每一个分块文件的路径，放入list，因为list是有序的
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        /*List<ComposeSource> composeSources = new ArrayList<>();
        for (int i = 0; i < chunkTotal; i++) {
            ComposeSource composeSource = ComposeSource.builder()
                    .bucket(bucket_video)
                    .object(chunkFileFolderPath + i)
                    .build();
            composeSources.add(composeSource);
        }*/
        List<ComposeSource> composeSources = Stream
                .iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket_video)
                        .object(chunkFileFolderPath + i)
                        .build())
                .collect(Collectors.toList());

        //合并分块文件
        try {
            minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucket_video)
                            .sources(composeSources)
                            .object(objectName)
                            .build());
            log.debug("合并文件成功:{}", objectName);
        } catch (Exception e) {
            log.debug("合并文件失败,fileMd5:{},异常:{}", fileMd5, e.getMessage(), e);
            return RestResponse.validfail(false, "文件合并失败");
        }

        /*----------------------------------------------文件完整性校验----------------------------------------------*/
        //下载合并后的文件
        File fileFromMinIO = downloadFileFromMinIO(bucket_video, objectName);
        if (fileFromMinIO == null) {
            //下载失败，说明上传后，出现异常
            return RestResponse.validfail(false, "文件上传异常");
        }
        //转为流，获取md5值后与传入的fileMd5进行比对
        try (InputStream fileFromMinIOInputStream = Files.newInputStream(fileFromMinIO.toPath())) {
            String md5Hex = DigestUtils.md5Hex(fileFromMinIOInputStream);
            if (!StringUtils.equals(md5Hex, fileMd5)) {
                return RestResponse.validfail(false, "文件上传失败：文件校验失败，数据不完整或被篡改");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);

        }

        /*----------------------------------------------清除分块文件----------------------------------------------*/
        clearChunkFiles(chunkFileFolderPath, chunkTotal);

        /*----------------------------------------------文件信息入库----------------------------------------------*/
        //设置文件大小
        uploadFileParamsDto.setFileSize(fileFromMinIO.length());
        //存入数据库
        currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_video, objectName);

        //返回
        return RestResponse.success(true);
    }

    /**
     * 得到分块文件的目录
     *
     * @param fileMd5 文件的md5值
     * @return 分块文件的目录
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    /**
     * 得到合并后的文件的地址
     *
     * @param fileMd5 文件id即md5值
     * @param fileExt 文件扩展名
     * @return
     */
    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

    public File downloadFileFromMinIO(String bucket, String objectName) {
        //临时文件
        File minioFile;
        FileOutputStream outputStream = null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //创建临时文件
            minioFile = File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 清除分块文件
     *
     * @param chunkFileFolderPath 分块文件路径
     * @param chunkTotal          分块文件总数
     */
    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal) {

        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("video").objects(deleteObjects).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r -> {
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清楚分块文件失败,objectname:{}", deleteError.objectName(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清楚分块文件失败,chunkFileFolderPath:{}", chunkFileFolderPath, e);
        }
    }


}
