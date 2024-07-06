package com.lartimes.weather;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/7/1 22:18
 */
@ConfigurationPropertiesScan(basePackages = {"com.lartimes.weather.conf"})
@SpringBootApplication
@Import(DruidDataSourceAutoConfigure.class)
@MapperScan("com.lartimes.weather.mapper")
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class);
    }
}
