package io.github.planegg.allone.starter.entity.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.planegg.allone.starter.entity.po.MsSerialGenerateEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 序列生成表 Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2023-01-27
 */
@Repository("io.github.planegg.allone.starter.entity.mapper.MsSerialGenerateMapper")
public interface MsSerialGenerateMapper extends BaseMapper<MsSerialGenerateEntity> {

    /**
     * 根据参数以悲观锁的方式查询数据
     * @param msIdGenerateEntityWrapper
     * @return
     */
    List<MsSerialGenerateEntity> selectByParmWithLock (@Param(Constants.WRAPPER) Wrapper<MsSerialGenerateEntity> msIdGenerateEntityWrapper);

}
