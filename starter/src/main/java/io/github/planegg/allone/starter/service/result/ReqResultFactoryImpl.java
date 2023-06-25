package io.github.planegg.allone.starter.service.result;

import io.github.planegg.allone.starter.common.util.PrjStringUtil;
import io.github.planegg.allone.starter.config.ReqResultValueConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class ReqResultFactoryImpl implements IReqResultFactory {

    @Autowired
    private ReqResultValueConfig reqResultValueConfig;

    @Override
    public ReqResultDti createSuccessMsg(Object data , Enum info, Object... parms) {
        ReqResultVo reqResultVo = createReqResultVo(info,parms);
        reqResultVo.setRsFlag(reqResultValueConfig.getRsFlagS());
        reqResultVo.setData(data);
        return reqResultVo;
    }



    @Override
    public ReqResultDti createSuccessMsg(Object data) {
        return createSuccessMsg(data,null);
    }


    @Override
    public ReqResultDti createFailMsg(Enum info, Object... parms) {
        ReqResultVo reqResultVo = createReqResultVo(info,parms);
        reqResultVo.setRsFlag(reqResultValueConfig.getRsFlagF());
        return reqResultVo;
    }

    @Override
    public ReqResultDti createFailMsg(String errMsg, Object... parms) {
        errMsg = PrjStringUtil.formatForLog(errMsg,parms);
        ReqResultVo reqResultVo = new ReqResultVo(reqResultValueConfig.getRsFlagF(),null,errMsg,null);
        return reqResultVo;
    }

    /**
     * 创建请求消息体
     * @param info
     * @param parms
     * @return
     */
    private ReqResultVo createReqResultVo(Enum info, Object... parms){
        String msg = reqResultValueConfig.getSuccessMsg();
        String code = null;
        if (info != null){
            IResultMsgKeyDti prjInfo = (IResultMsgKeyDti) info;
            msg = PrjStringUtil.formatForLog(prjInfo.getMsg(),parms);
            code = prjInfo.getCode();
        }
        ReqResultVo reqResultVo = new ReqResultVo(code,msg);
        return reqResultVo;
    }
}
