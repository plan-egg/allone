package io.github.planegg.allone.starter.exception;

public class ItHandleException extends PrjBaseRuntimeException{

    public ItHandleException(String errMsg,Object... msgParm) {
        super(errMsg,msgParm);
    }
}
