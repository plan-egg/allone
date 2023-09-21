package io.github.planegg.flywayapp;

import io.github.planegg.flywayapp.service.FlywayClientServiceImpl;
import io.github.planegg.flywayapp.service.IFlywayClientService;

public class FlywayExecutor {

    public static void main(String[] args) {
        IFlywayClientService flywayClientService = new FlywayClientServiceImpl();

        flywayClientService.initFlywayClient();
        flywayClientService.execute();
    }
}
