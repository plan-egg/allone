package io.github.planegg.allone.starter.service.result;

/**
 *
 */
public interface IReqResultFactory {
    /**
     *
     * @param data
     * @param info
     * @param parms
     * @return
     */
    ReqResultDti createSuccessMsg(Object data, Enum info, Object... parms);

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
    ReqResultDti createFailMsg(Enum info, Object... parms);

    /**
     *
     * @param errMsg
     * @param parms
     * @return
     */
    ReqResultDti createFailMsg(String errMsg, Object... parms);
}
