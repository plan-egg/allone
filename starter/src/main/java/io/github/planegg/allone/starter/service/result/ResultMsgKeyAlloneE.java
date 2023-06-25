package io.github.planegg.allone.starter.service.result;

/**
 *
 */
public enum ResultMsgKeyAlloneE implements IResultMsgKeyDti {
    MISS_ARG("缺少请求参数：{}")
    ,DATA_CHECK("数据验证异常：{}")
    ,DATA_BIND("参数名称或传递方式不正确：{}")
    ,LOGIN_EXP("-1","登录过期：{}")

    ;
    private String rsFlag;
    private String code;
    private String msg;

    ResultMsgKeyAlloneE(String code, String msg) {
        this.rsFlag = "F";
        this.code = code;
        this.msg = msg;
    }


    ResultMsgKeyAlloneE(String msg) {
        this.rsFlag = "F";
        this.code = "0";
        this.msg = msg;
    }


    @Override
    public String getRsFlag() {
        return rsFlag;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

}
