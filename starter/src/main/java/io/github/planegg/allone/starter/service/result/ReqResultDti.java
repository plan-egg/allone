package io.github.planegg.allone.starter.service.result;

/**
 * 请求结果声明
 * @param <T>
 * @param <E>
 */
public interface ReqResultDti <T,E extends Enum & IResultMsgKeyDti>{
    /**
     * 获取请求成功标识
     * @return
     */
    String getRsFlag();

    /**
     * 获取返回编码
     * @return
     */
    String getCode();

    /**
     * 获取返回结果消息
     * @return
     */
    String getMsg() ;

    /**
     * 获取请求成功返回的数据
     * @return
     */
    T getData() ;

}
