package io.github.planegg.allone.starter.service.result;

/**
 *
 * @param <T>
 * @param <E>
 */
class ReqResultVo <T,E extends Enum & IResultMsgKeyDti> implements ReqResultDti {


    /**
     * 是否成功
     * S：成功
     * 其他：失败
     */
    private String rsFlag;
    private String code;
    private String msg;
    private T data;

    ReqResultVo(String rsFlag, String code, String msg, T data) {
        this.rsFlag = rsFlag;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    ReqResultVo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    void setRsFlag(String rsFlag) {
        this.rsFlag = rsFlag;
    }

    void setData(T data) {
        this.data = data;
    }

    @Override
    public String getRsFlag() {
        return rsFlag;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public T getData() {
        return data;
    }

}
