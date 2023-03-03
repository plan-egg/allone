package io.github.planegg.allone.starter.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * starter入口
 */
@Configuration
@ComponentScan({"io.github.planegg.allone.starter"})
@MapperScan({"io.github.planegg.allone.starter.entity.mapper"})
public class StarterAutoConfigure {


}
