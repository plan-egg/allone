package io.github.planegg.allone.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@MapperScan({"io.github.planegg.allone.starter.entity.mapper"})
@EnableOpenApi
@EnableWebMvc
@Configuration
public class BootApplication {
    public static void main(String[] args) {
        System.out.println("swagger默认访问路径：/swagger-ui/index.html");

        SpringApplication.run(BootApplication.class, args);
    }
}
