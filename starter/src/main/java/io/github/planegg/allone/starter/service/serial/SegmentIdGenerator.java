package io.github.planegg.allone.starter.service.serial;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class SegmentIdGenerator implements IdentifierGenerator {

    @Autowired
    @Lazy
    private ISerialGenerateService serialGenerateService;



    @Override
    public Number nextId(Object entity) {
        return  serialGenerateService.getId(entity);
    }
}
