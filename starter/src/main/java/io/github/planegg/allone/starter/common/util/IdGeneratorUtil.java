package io.github.planegg.allone.starter.common.util;

import java.util.UUID;

/**
 *
 */
public class IdGeneratorUtil {
    /**
     * 获取去除"-"的短UUID
     * @return
     */
    public static String getUuidS(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
