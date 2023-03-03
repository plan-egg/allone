package io.github.planegg.allone.starter.service.cache;

import io.github.planegg.allone.starter.common.util.PrjStringUtil;
import io.github.planegg.allone.starter.exception.ItHandleException;
import io.github.planegg.allone.starter.service.lock.IDistributedLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * redis实现的缓存服务
 */
public class RedisServiceImpl implements ICacheService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IDistributedLockService distributedLockService;

    private RedisServiceImpl() {
    }

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    private final static String KEY_SEPARATOR = ":";

    @Override
    public <T extends Enum & ICacheKeyDti> String getKeyStr(T keyE, String keyExt){
        String keyStr =  keyE.getKeyGroup() + KEY_SEPARATOR + keyE.name();
        if (!PrjStringUtil.isEmpty(keyExt)){
            keyStr = keyStr + KEY_SEPARATOR + keyExt;
        }
        return keyStr;
    }

    @Override
    public <T extends Enum & ICacheKeyDti> String getKeyStr(T keyE){
        return getKeyStr(keyE,null);
    }

    @Override
    public <T extends Enum & ICacheKeyDti,E> E get(T keyE, Class<E> valClzType) {
        return get(keyE,null,valClzType);
    }

    @Override
    public <T extends Enum & ICacheKeyDti, E> E get(T keyE, String keyExt, Class<E> valClzType) {
        String keyStr = getKeyStr(keyE,keyExt);
        E valObj = valClzType.cast(redisTemplate.opsForValue().get(keyStr)) ;
        return valObj;
    }

    @Override
    public <T extends Enum & ICacheKeyDti>  String getString(T keyE) {
        return get( keyE,String.class);
    }

    @Override
    public <T extends Enum & ICacheKeyDti> String getString(T keyE, String keyExt) {
        return get(keyE,keyExt,String.class);
    }


    @Override
    public <T extends Enum & ICacheKeyDti,E> void set(T keyE, String keyExt, E val) {
        String keyStr = getKeyStr(keyE,keyExt);
        Class valClzType = keyE.getValClzType();
        if (valClzType != null && valClzType.isAssignableFrom(Long.class)){
            throw new ItHandleException("redis的set方法不支持Long类型，请使用 String !key={}",keyStr);
        }
        int expTime = keyE.getExpTime();
        TimeUnit expTimeUnit = keyE.getExpTimeUnit();
        if (expTime == 0){
            throw new ItHandleException("the expTime of redis key [{}] must be greater than 0 !",keyStr);
        }

        if (! valClzType.isInstance(val)){
            throw new ItHandleException("the value of redis key [{}] must match with {} !",keyStr,valClzType.getName());
        }
        redisTemplate.opsForValue().set(keyStr,valClzType.cast(val),expTime, expTimeUnit);
    }

    @Override
    public <T extends Enum & ICacheKeyDti> Boolean deleteKey(T keyE) {
        return deleteKey(keyE,null);
    }

    @Override
    public <T extends Enum & ICacheKeyDti> Boolean deleteKey(T keyE, String keyExt) {
        String keyStr = getKeyStr(keyE,keyExt);
        return redisTemplate.delete(keyStr);
    }

    @Override
    public <T extends Enum & ICacheKeyDti,E> void set(T keyE, E val) {
        set( keyE,null,val);
    }

    @Override
    public <T extends Enum & ICacheKeyDti>  Long increment(T keyE){
        return increment(keyE,null);
    }
    @Override
    public <T extends Enum & ICacheKeyDti>  Long increment(T keyE, String keyExt){
        String keyStr = getKeyStr(keyE,keyExt);
        int expTime = keyE.getExpTime();
        TimeUnit expTimeUnit = keyE.getExpTimeUnit();
        Long increVal = redisTemplate.opsForValue().increment(keyStr);
        if (expTime == 0){
            throw new ItHandleException("the expTime of redis key [{}] must be greater than 0 !",keyStr);
        }
        redisTemplate.expire(keyStr,expTime,expTimeUnit);
        return increVal;
    }

    @Override
    public <T extends Enum & ICacheKeyDti,E> E getFromDbUsingCache(T keyE, String keyExt, Class<E> valClzType
            , Function<String, E> bizService ){
        E rsInCache = get(keyE,keyExt,valClzType);
        if (rsInCache != null ){
            return rsInCache;
        }
        String keyStr = getKeyStr(keyE,keyExt);
        E rsInDb = bizService.apply(keyStr);
        if (rsInDb == null){
            throw new ItHandleException("从数据库里没有查找到目标数据，keyStr={}",keyStr);
        }
        rsInCache = get(keyE,keyExt,valClzType);
        if (rsInCache != null ){
            return rsInCache;
        }
        set(keyE,keyExt,rsInDb);
        return rsInDb;
    }
    @Override
    public <T extends Enum & ICacheKeyDti,E> boolean refreshDbAndCache(T keyE, String keyExt, E newVal
            , BiFunction<String, E , Boolean> bizService ){

        String keyStr = getKeyStr(keyE,keyExt);
        Boolean updateOk = bizService.apply(keyStr,newVal);
        if (!updateOk ){
            throw new ItHandleException("从数据库里没有查找到目标数据，keyStr={}",keyStr);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("更新缓存时，唤醒线程失败！key={}",keyStr);
            updateOk = false;
        }finally {
            deleteKey(keyE,keyExt);
            return updateOk;
        }

    }


}
