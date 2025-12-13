package ru.practicum;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public StatsClient statsClient() {
        return new StatsClient();
    }
}
