package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 全局异常处理器
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 自定义异常的捕获及处理
     *
     * @param exception 自定义异常
     * @return 预错误提示
     */
    @ResponseBody
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException exception) {
        log.error("【系统异常】{}", exception.getErrMessage(), exception);
        return new RestErrorResponse(exception.getErrMessage());
    }

    /**
     * 系统异常的捕获及处理
     * @param exception 系统异常
     * @return 通用出错提示
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception exception) {
        log.error("【系统异常】{}", exception.getMessage(), exception);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }


}
