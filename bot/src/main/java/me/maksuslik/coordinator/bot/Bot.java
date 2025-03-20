package me.maksuslik.coordinator.bot;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import me.maksuslik.coordinator.configuration.properties.TelegramBotProperties;
import me.maksuslik.coordinator.handler.command.impl.SendEmailCommandHandler;
import me.maksuslik.coordinator.handler.command.impl.StartCommandHandler;
import me.maksuslik.coordinator.handler.command.impl.ToggleNotificationsCommandHandler;
import me.maksuslik.coordinator.service.command.CommandServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Data
@Component
@Slf4j
public class Bot implements InitializingBean {

    TelegramBotProperties botProperties;
    TelegramBotsLongPollingApplication telegramBotsApplication;

    @Getter
    TelegramClient client;

    @Autowired
    private CommandServiceImpl commandService;

    public Message sendMessage(Long chatId, String text, boolean useMarkdown) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);
        if (useMarkdown)
            sendMessage.setParseMode(ParseMode.MARKDOWN);

        try {
            return client.execute(sendMessage);
        } catch (TelegramApiException exception) {
            log.error("Не удалось отправить сообщение!", exception);
        }

        return null;
    }

    public Message sendMessage(Long chatId, String text) {
        return sendMessage(chatId, text, true);
    }

    @Override
    public void afterPropertiesSet() {
        commandService.registerCommand(StartCommandHandler.class);
        commandService.registerCommand(SendEmailCommandHandler.class);
        commandService.registerCommand(ToggleNotificationsCommandHandler.class);
    }
}
