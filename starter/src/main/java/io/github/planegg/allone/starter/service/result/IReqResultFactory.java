package io.github.planegg.allone.starter.service.result;

/**
 *
 */
public interface IReqResultFactory <E extends Enum & IResultMsgKeyDti> {
    /**
     *
     * @param data
     * @param info
     * @param parms
     * @return
     */
    ReqResultDti createSuccessMsg(Object data, E info, Object... parms);

    /**
     *
     * @param data
     * @return
     */
    ReqResultDti createSuccessMsg(Object data);

    /**
     *
     * @param info
     * @param parms
     * @return
     */
    ReqResultDti createFailMsg(E info, Object... parms);

    /**
     *
     * @param errMsg
     * @param parms
     * @return
     */
    ReqResultDti createFailMsg(String errMsg, Object... parms);
}
