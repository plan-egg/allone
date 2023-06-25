package io.github.planegg.allone.starter.service.cache;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 缓存服务
 */
public interface ICacheService {

    /**
     * 获取key存放于redis的key名（可动态传入扩展key）
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @param keyExt 扩展key值，用于动态接收key值，如key  【misc:orderNO:230101】 ，这里的 230101 就是动态传入的。
     * @return
     */
    <T extends Enum & ICacheKeyDti> String getKeyStr(T keyE, String... keyExt);
    /**
     * 获取key存放于redis的key名
     * @param keyE
     * @param <T>
     * @return
     */
    <T extends Enum & ICacheKeyDti> String getKeyStr(T keyE);
    /**
     * 根据key获取缓存值
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @param valClzType 缓存值类型，一般使用枚举类定义的值即可
     * @return
     */
    <T extends Enum & ICacheKeyDti,E> E get(T keyE, Class<E> valClzType);
    /**
     * 根据key获取缓存值
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @param keyExt 扩展key值，用于动态接收key值，如key  【misc:orderNO:230101】 ，这里的 230101 就是动态传入的。
     * @param valClzType 缓存值类型，一般使用枚举类定义的值即可
     * @return
     */
    <T extends Enum & ICacheKeyDti,E> E get(Class<E> valClzType , T keyE, String... keyExt );

    /**
     * 根据key获取缓存值（字符串）
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @return
     */
    <T extends Enum & ICacheKeyDti>  String getString(T keyE);


    /**
     * 根据key获取缓存值（字符串）
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @param keyExt 扩展key值，用于动态接收key值，如key  【misc:orderNO:230101】 ，这里的 230101 就是动态传入的。
     * @return
     */
    <T extends Enum & ICacheKeyDti>  String getString(T keyE, String... keyExt );

    /**
     * 设置缓存值（可动态传入扩展key）
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @param keyExt 扩展key值，用于动态接收key值，如key  【misc:orderNO:230101】 ，这里的 230101 就是动态传入的。
     * @param val 缓存值
     * @param <T> 缓存的key的类型
     * @param <E> 缓存值数据类型
     */
    <T extends Enum & ICacheKeyDti,E> void set(E val ,T keyE, String... keyExt );


    /**
     * 设置缓存值
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @param val 缓存值
     * @param <T> 缓存的key的类型
     * @param <E> 缓存值数据类型
     */
    <T extends Enum & ICacheKeyDti,E> void set(E val ,T keyE );
    /**
     * 删除缓存
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @param <T> 缓存的key的类型
     */
    <T extends Enum & ICacheKeyDti> Boolean deleteKey(T keyE);
    /**
     * 删除缓存（可动态传入扩展key）
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @param keyExt 扩展key值，用于动态接收key值，如key  【misc:orderNO:230101】 ，这里的 230101 就是动态传入的。
     * @param <T> 缓存的key的类型
     */
    <T extends Enum & ICacheKeyDti> Boolean deleteKey(T keyE, String... keyExt);

    /**
     * 获取自增序列
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @return
     */
    <T extends Enum & ICacheKeyDti>  Long increment(T keyE);
    /**
     * 获取自增序列
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @param keyExt 扩展key值，用于动态接收key值，如key  【misc:orderNO:230101】 ，这里的 230101 就是动态传入的。
     * @return
     */
    <T extends Enum & ICacheKeyDti>  Long increment(T keyE, String... keyExt);

    /**
     * 从缓存中获取数据库的值，如果没有，则从数据库中获取，并存于缓存
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @param keyExt 扩展key值，用于动态接收key值，如key  【misc:orderNO:230101】 ，这里的 230101 就是动态传入的。
     * @param valClzType 缓存值类型，一般使用枚举类定义的值即可
     * @param bizService 业务方法，实现从数据库获取数据，返回用于存放在缓存的业务逻辑
     * @return
     */
    <T extends Enum & ICacheKeyDti,E> E getFromDbUsingCache( Class<E> valClzType, Function<String, E> bizService ,T keyE, String... keyExt);
    /**
     * 更新数据库及缓存中的值
     * @param keyE 缓存的key，枚举类，且必须实现ICacheKeyService接口
     * @param keyExt 扩展key值，用于动态接收key值，如key  【misc:orderNO:230101】 ，这里的 230101 就是动态传入的。
     * @param newVal 新设置的值
     * @param bizService 业务方法，实现从数据库获取数据，返回用于存放在缓存的业务逻辑
     * @return
     */
    <T extends Enum & ICacheKeyDti,E> boolean refreshDbAndCache(E newVal, BiFunction<String, E , Boolean> bizService
            , T keyE, String... keyExt );

}
