package io.github.planegg.allone.starter.service.serial;

import io.github.planegg.allone.starter.common.constant.CacheKeyAlloneE;
import io.github.planegg.allone.starter.common.constant.DistributedLockKeyAlloneE;
import io.github.planegg.allone.starter.service.cache.ICacheService;
import io.github.planegg.allone.starter.service.lock.IDistributedLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public abstract class ABaseSerialGenerateService implements ISerialGenerateService {

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private IDistributedLockService distributedLockService;



    @Override
    public long getId(Object entity) {
        return getSerial("table_id");
    }

    @Override
    public long getSerial(String serialCode) {
        Long nextSerial = distributedLockService.getWithTryLock(this ::getSerialWithLock, serialCode,String.class,Long.class
                , DistributedLockKeyAlloneE.serial_get_batch_$,serialCode);
        return nextSerial;
    }


    protected Long getSerialWithLock(String serialCode){
        Long nextSerial = getNextSerial(serialCode);
        if (nextSerial != null){
            return nextSerial;
        }
        nextSerial = getNextBatchSerialFromDb(serialCode);
        return nextSerial;
    }

    /**
     * 获取下一个序列号
     * @return
     */
    protected Long getNextSerial(String serialCode){
        String idBatchStrNumStr = cacheService.getString(CacheKeyAlloneE.serial_batch_str_num_$,serialCode);
        if (idBatchStrNumStr == null){
            return null;
        }
        String idBatchEndNumStr = cacheService.getString(CacheKeyAlloneE.serial_batch_end_num_$,serialCode);
        if (idBatchEndNumStr == null){
            return null;
        }
        Long idBatchStrNum = Long.valueOf(idBatchStrNumStr);
        Long idBatchEndNum = Long.valueOf(idBatchEndNumStr);
        Long countNum = cacheService.increment(CacheKeyAlloneE.serial_batch_count_num,serialCode);
        if (countNum == null){
            return null;
        }
        Long currentId = idBatchStrNum + countNum;
        if (currentId <= idBatchEndNum){
            return currentId;
        }
        return null;
    }

    /**
     *
     * @param serialCode
     * @return
     */
    protected abstract Long getNextBatchSerialFromDb(String serialCode);

}
