package io.github.planegg.allone.starter.service.cache;

import java.util.concurrent.TimeUnit;

public interface ICacheKeyDti {
    /**
     * 获取key分组
     * 一般用系统/模块标识作为分组名
     * @return
     */
    String getKeyGroup();
    /**
     * 获取缓存值的类型
     * @return
     */
    Class getValClzType();

    /**
     * 获取缓存过期时间值（默认秒）
     * @return
     */
    int getExpTime();
    /**
     * 获取缓存过期时间单位（默认秒）
     * @return
     */
    TimeUnit getExpTimeUnit();
}
