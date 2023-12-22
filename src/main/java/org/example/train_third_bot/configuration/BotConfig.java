package org.example.train_third_bot.configuration;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {


    @Value("${bot.username}")
    public String botUsername;

    @Value("${bot.token}")
    public String botToken;

    @Value("${bot.ownerId}")
    public long botId;


}
