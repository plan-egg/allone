package io.github.planegg.allone.starter.exception;

import io.github.planegg.allone.starter.common.util.PrjStringUtil;
import io.github.planegg.allone.starter.service.result.IResultMsgKeyDti;

public class PrjBaseRuntimeException extends RuntimeException {
    /**
     * 异常消息编码
     */
    protected String code;

    /**
     *
     * @param errMsg
     * @param msgParm
     */
    public PrjBaseRuntimeException(String errMsg,Object... msgParm) {
        super(PrjStringUtil.formatForLog(errMsg,msgParm));
        if (msgParm.length > 0 && msgParm[msgParm.length - 1] instanceof Throwable){
            super.initCause((Throwable) msgParm[msgParm.length - 1]);
        }
    }

    /**
     *
     * @param errInfoE
     * @param msgParm
     * @param <T>
     */
    public <T extends Enum & IResultMsgKeyDti> PrjBaseRuntimeException(T errInfoE , Object... msgParm) {
        this(errInfoE.getMsg(), msgParm);
        this.code = errInfoE.getCode();
    }

    /**
     *
     * @return
     */
    public String getCode() {
        return code;
    }
}
