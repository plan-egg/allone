package io.github.planegg.allone.starter.service.serial;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.planegg.allone.starter.common.constant.CacheKeyAlloneE;
import io.github.planegg.allone.starter.common.util.PrjStringUtil;
import io.github.planegg.allone.starter.entity.mapper.MsSerialGenerateMapper;
import io.github.planegg.allone.starter.entity.po.MsSerialGenerateEntity;
import io.github.planegg.allone.starter.exception.ItHandleException;
import io.github.planegg.allone.starter.service.cache.ICacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Service("serialGenerateService")
@ConditionalOnMissingBean(name = "serialGenerateService")
public class SerialGenerateServiceImpl extends BaseSerialGenerateServiceImpl implements ISerialGenerateService {

    @Autowired
    private ICacheService cacheService;

    @Autowired
    @Qualifier("io.github.planegg.allone.starter.entity.mapper.MsSerialGenerateMapper")
    private MsSerialGenerateMapper msSerialGenerateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long getNextBatchSerialFromDb(String serialCode){
/*        Long serialNum = getNextSerial(serialCode);
        if (serialNum != null){
            return serialNum;
        }*/

        Long serialNum = null;

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

        cacheService.set(String.valueOf(msSerialGenerateEntityDb.getMaxNum()),CacheKeyAlloneE.serial_batch_str_num_$,serialCode);

        Long nextMaxNum = msSerialGenerateEntityDb.getMaxNum() + msSerialGenerateEntityDb.getBatchNum();
        cacheService.set( String.valueOf(nextMaxNum) , CacheKeyAlloneE.serial_batch_end_num_$,serialCode);
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
