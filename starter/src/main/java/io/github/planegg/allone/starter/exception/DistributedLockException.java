package io.github.planegg.allone.starter.exception;

public class DistributedLockException extends PrjBaseRuntimeException{

    public DistributedLockException(String errMsg, Object... msgParm) {
        super(errMsg,msgParm);
    }

}
