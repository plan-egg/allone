package io.github.planegg.flywayapp.service;

import com.alibaba.fastjson.JSONObject;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.Configuration;

import java.io.File;
import java.util.List;

/**
 * flyway客户端服务
 */
public interface IFlywayClientService {
    /**
     * 获取客户端
     * @return
     */
    Flyway getFlywayClient();

    /**
     * 执行flyway命令
     * @param flyway
     */
    void execute(Flyway flyway);

    /**
     * 获取配置
     * @return
     */
    Configuration getConfig(List<JSONObject> configList);

    /**
     * 根据配置文件转换成配置对象
     * @return
     */
    List<JSONObject> getConfigList ();

    /**
     * 读取配置文件
     * @param configFile
     * @return
     */
    JSONObject readFromFile(File configFile);



}
