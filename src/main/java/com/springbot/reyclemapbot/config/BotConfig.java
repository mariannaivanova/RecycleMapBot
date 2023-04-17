package com.springbot.reyclemapbot.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@Getter
@PropertySource("classpath:application.properties")
public class BotConfig {
    @Value("${bot.username}") private String botUsername;

    @Value("${bot.token}") private String token;

}