package io.github.planegg.flywayapp;

import io.github.planegg.flywayapp.service.FlywayClientServiceImpl;
import io.github.planegg.flywayapp.service.IFlywayClientService;
import org.flywaydb.core.Flyway;

public class FlywayExecutor {

    public static void main(String[] args) {
        IFlywayClientService flywayClientService = new FlywayClientServiceImpl();

        Flyway flywayClient = flywayClientService.getFlywayClient();
        flywayClientService.execute(flywayClient);
    }
}
