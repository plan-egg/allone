package io.github.planegg.allone.starter.service.serial;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.planegg.allone.starter.common.constant.CacheKeyAlloneE;
import io.github.planegg.allone.starter.common.constant.DistributedLockKeyAlloneE;
import io.github.planegg.allone.starter.common.util.PrjStringUtil;
import io.github.planegg.allone.starter.entity.mapper.MsSerialGenerateMapper;
import io.github.planegg.allone.starter.entity.po.MsSerialGenerateEntity;
import io.github.planegg.allone.starter.exception.ItHandleException;
import io.github.planegg.allone.starter.service.cache.ICacheService;
import io.github.planegg.allone.starter.service.lock.IDistributedLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

/**
 *
 */
@Service("serialGenerateService")
public class SerialGenerateServiceImpl implements ISerialGenerateService {

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private IDistributedLockService distributedLockService;

    @Autowired
    @Qualifier("io.github.planegg.allone.starter.entity.mapper.MsSerialGenerateMapper")
    private MsSerialGenerateMapper msSerialGenerateMapper;



    @Override
    public long getId() {
        return getSerial("table_id");
    }

    @Override
    public long getSerial(String serialCode) {

        Long nextSerial = getNextSerial(serialCode);
        if (nextSerial != null){
            return nextSerial;
        }
        Function<String, Long> getNextBatchIdFromDb =this ::getNextBatchSerialFromDb;
        nextSerial = distributedLockService.getWithTryLock(DistributedLockKeyAlloneE.serial_get_batch,getNextBatchIdFromDb, serialCode,String.class,Long.class );
        return nextSerial;
    }

    /**
     * 获取下一个序列号
     * @return
     */
    private Long getNextSerial(String serialCode){
        String idBatchStrNumStr = cacheService.getString(CacheKeyAlloneE.serial_batch_str_num,serialCode);
        if (idBatchStrNumStr == null){
            return null;
        }
        String idBatchEndNumStr = cacheService.getString(CacheKeyAlloneE.serial_batch_end_num,serialCode);
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
    @Transactional(rollbackFor = Exception.class)
    Long getNextBatchSerialFromDb(String serialCode){
        Long serialNum = getNextSerial(serialCode);
        if (serialNum != null){
            return serialNum;
        }

        QueryWrapper<MsSerialGenerateEntity> queryWrapper = new QueryWrapper<>();
        String serialCodeParm = serialCode;
        if (PrjStringUtil.isEmpty(serialCode)) {
            serialCodeParm = "default";
        }
        queryWrapper.lambda().eq(MsSerialGenerateEntity::getSerialCode, serialCodeParm );
        List<MsSerialGenerateEntity> msSerialGenerateEntityList = msSerialGenerateMapper.selectByParmWithLock(queryWrapper);
        if (msSerialGenerateEntityList == null || msSerialGenerateEntityList.size() == 0) {
            throw new ItHandleException("id生成出错，没有在表在找到记录，key={}", serialCodeParm);
        }

        MsSerialGenerateEntity msSerialGenerateEntityDb = msSerialGenerateEntityList.get(0);
        cacheService.set(CacheKeyAlloneE.serial_batch_str_num,serialCode, String.valueOf(msSerialGenerateEntityDb.getMaxNum()));
        Long nextMaxNum = msSerialGenerateEntityDb.getMaxNum() + msSerialGenerateEntityDb.getBatchNum();
        cacheService.set(CacheKeyAlloneE.serial_batch_end_num,serialCode, String.valueOf(nextMaxNum));
        cacheService.deleteKey(CacheKeyAlloneE.serial_batch_count_num,serialCode);

        msSerialGenerateEntityDb.setMaxNum(nextMaxNum);
        msSerialGenerateMapper.updateById(msSerialGenerateEntityDb);

        serialNum = getNextSerial(serialCode);
        if (serialNum != null){
            return serialNum;
        }
        throw new ItHandleException("出现未知错误，未能正确生成序列号，key={}",serialCodeParm);
    }
}
