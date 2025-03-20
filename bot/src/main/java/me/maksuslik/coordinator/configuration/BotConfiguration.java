package me.maksuslik.coordinator.configuration;

import me.maksuslik.coordinator.bot.BotRequestProcessor;
import me.maksuslik.coordinator.configuration.properties.TelegramBotProperties;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.starter.TelegramBotInitializer;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collections;
import java.util.List;

@Configuration
public class BotConfiguration {
    @Bean
    public BotRequestProcessor botRequestProcessor() {
        return new BotRequestProcessor();
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsLongPollingApplication() {
        return new TelegramBotsLongPollingApplication();
    }

    @Bean
    public TelegramClient telegramClient(OkHttpClient okHttpClient, TelegramBotProperties properties) {
        return new OkHttpTelegramClient(okHttpClient, properties.getToken());
    }

    @Bean
    public SpringLongPollingBot springLongPollingBot(TelegramBotProperties properties, BotRequestProcessor requestProcessor) {
        return new SpringLongPollingBot() {
            @Override
            public String getBotToken() {
                return properties.getToken();
            }

            @Override
            public LongPollingUpdateConsumer getUpdatesConsumer() {
                return updates -> updates.forEach(requestProcessor::handleUpdate);
            }
        };
    }

    @Bean
    public TelegramBotInitializer telegramBotInitializer(TelegramBotsLongPollingApplication telegramBotsApplication,
                                                         ObjectProvider<List<SpringLongPollingBot>> longPollingBots) {
        return new TelegramBotInitializer(telegramBotsApplication,
                longPollingBots.getIfAvailable(Collections::emptyList));
    }
}