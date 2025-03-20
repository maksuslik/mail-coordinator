package me.maksuslik.coordinator.configuration.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Component
@ConfigurationProperties(prefix = "app.bot.telegram")
public class TelegramBotProperties {

    String username;
    String token;
}
