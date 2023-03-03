package io.github.planegg.allone.starter.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 项目工具：字符串工具
 */
public class PrjStringUtil {

    private final static Logger logger = LoggerFactory.getLogger(PrjStringUtil.class);
    /**
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        return StringUtils.isEmpty(str);
    }

    /**
     * 日志格式化
     * 占位符为 {}
     * @param logMsg
     * @param parms
     * @return
     */
    public static String formatForLog(String logMsg,Object[] parms){
        if (logMsg == null ){
            return "";
        }
        if (parms == null || parms.length == 0){
            return logMsg;
        }
        try {
            for (Object aParm : parms){
                if (aParm == null){
                    aParm = "[null]";
                }
                if (aParm instanceof Throwable){
                    break;
                }

                logMsg = logMsg.replaceFirst("\\{\\}",(String)aParm);
            }
        }catch (Exception e){
            logger.error("日志格式化失败，日志模板：{}",logMsg);
        }
        return logMsg;
    }

}
