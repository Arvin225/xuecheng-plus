package com.xuecheng.base.exception;

public class XueChengPlusException extends RuntimeException{
    private String errMessage;

    public XueChengPlusException(){
        super();
    }

    public XueChengPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    /**
     * 通过自定义异常抛出通用错误提示
     * @param commonError 通用错误提示
     */
    public static void cast(CommonError commonError){
        throw new XueChengPlusException(commonError.getErrMessage());
    }

    /**
     * 通过自定义异常抛出预知错误提示
     * @param errMessage
     */
    public static void cast(String errMessage){
        throw new XueChengPlusException(errMessage);
    }





}
