package com.xuecheng.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckCodeServiceClientFallbackFactory implements FallbackFactory<CheckCodeServiceClient> {
    @Override
    public CheckCodeServiceClient create(Throwable throwable) {
        return (key, code) -> {
            log.debug("调用验证码服务熔断异常:{}", throwable.getMessage());
            return null;
        };
    }
}