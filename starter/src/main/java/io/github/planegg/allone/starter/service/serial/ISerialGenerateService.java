package io.github.planegg.allone.starter.service.serial;

/**
 * 序列生成服务
 */
public interface ISerialGenerateService {
    /**
     * 获取id
     * @return
     */
    long getId(Object entity);

    /**
     * 获取序列
     * @param serialCode
     * @return
     */
    long getSerial(String serialCode);
}
