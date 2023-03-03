package io.github.planegg.allone.starter.exception;

/**
 * 业务处理异常
 * 用于告知用户应对异常的操作
 */
public class BizHandleException extends PrjBaseRuntimeException{

    public BizHandleException(String errMsg,Object... msgParm) {
        super(errMsg,msgParm);
    }
}
