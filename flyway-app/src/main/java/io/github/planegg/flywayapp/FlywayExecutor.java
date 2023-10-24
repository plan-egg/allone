package io.github.planegg.flywayapp;

import io.github.planegg.flywayapp.service.FlywayClientServiceImpl;
import io.github.planegg.flywayapp.service.IFlywayClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlywayExecutor {

    private final static Logger logger = LoggerFactory.getLogger(FlywayExecutor.class);

    public static void main(String[] args) {

        IFlywayClientService flywayClientService = new FlywayClientServiceImpl();
        try {
            flywayClientService.initFlywayClient();
            flywayClientService.execute();
        }catch (Exception e){
            logger.error("执行脚本报错！",e);
            throw e;
        }
    }
}
