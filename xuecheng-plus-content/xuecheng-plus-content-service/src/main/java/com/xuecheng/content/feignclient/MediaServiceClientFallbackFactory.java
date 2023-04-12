package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable throwable) {
        return (fileToUpload, folder, objectName) -> {
            //降级方法
            log.debug("调用媒资管理服务上传文件时发生熔断，异常信息:{}", throwable.toString(), throwable);
            return null;
        };
    }
}
