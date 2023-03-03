package io.github.planegg.allone.starter.service.result;

/**
 *
 */
public enum ResultMsgKeyAlloneE implements IResultMsgKeyDti {
    MISS_ARG("缺少请求参数：{}")
    ,DATA_CHECK("数据验证异常：{}")
    ,DATA_BIND("参数名称或传递方式不正确：{}")

    ;

    private String code;
    private String msg;

    ResultMsgKeyAlloneE(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    ResultMsgKeyAlloneE(String msg) {
        this.code = "-1";
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getMsg() {
        return null;
    }

}
