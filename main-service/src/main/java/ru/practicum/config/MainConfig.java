package ru.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.StatsClient;

@Configuration
public class MainConfig {
    @Bean
    public StatsClient statsClient() {
        return new StatsClient();
    }
}
