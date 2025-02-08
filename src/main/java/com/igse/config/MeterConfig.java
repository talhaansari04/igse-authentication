package com.igse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "meter.fixed-price")
public class MeterConfig {
    private double dayReading;
    private double nightReading;
    private double gasReading;
}
