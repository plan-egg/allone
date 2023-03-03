package io.github.planegg.allone.starter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 请求结果值定义
 */
@Configuration
@ConfigurationProperties(prefix = "allone.starter.req-rs")
public class ReqResultValueConfig {

    /**
     * 请求成功标识
     */
    @Value("${success:S}")
    private String  rsFlagS ;
    /**
     * 请求失败标识
     */
    @Value("${fail:F}")
    private String  rsFlagF ;
    /**
     * 请求成功通用消息
     */
    @Value("${success-msg:请求成功！}")
    private String  successMsg ;

    public String getRsFlagS() {
        return rsFlagS;
    }

    public void setRsFlagS(String rsFlagS) {
        this.rsFlagS = rsFlagS;
    }

    public String getRsFlagF() {
        return rsFlagF;
    }

    public void setRsFlagF(String rsFlagF) {
        this.rsFlagF = rsFlagF;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }
}
