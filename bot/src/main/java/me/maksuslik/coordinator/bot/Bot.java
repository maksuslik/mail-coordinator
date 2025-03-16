package me.maksuslik.coordinator.bot;

import me.maksuslik.coordinator.command.CommandManager;
import me.maksuslik.coordinator.command.SendEmailCommand;
import me.maksuslik.coordinator.command.StartCommand;
import me.maksuslik.coordinator.command.ToggleNotificationsCommand;
import me.maksuslik.coordinator.handler.ButtonHandler;
import me.maksuslik.coordinator.handler.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {
    public final Logger logger = LoggerFactory.getLogger(Bot.class);

    @Autowired
    private CommandManager commandManager;

    @Lazy
    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private ButtonHandler buttonHandler;

    @Value("${bot.username}")
    private String BOT_USERNAME;

    @Value("${bot.token}")
    private String BOT_TOKEN;

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public void onRegister() {
        commandManager.registerCommand(StartCommand.class);
        commandManager.registerCommand(SendEmailCommand.class);
        commandManager.registerCommand(ToggleNotificationsCommand.class);
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText())
            messageHandler.handle(update);

        if (update.hasCallbackQuery())
            buttonHandler.handle(update);
    }

    public Message sendMessage(Long chatId, String text, boolean useMarkdown) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);
        if (useMarkdown)
            sendMessage.setParseMode(ParseMode.MARKDOWN);

        try {
            return execute(sendMessage);
        } catch (TelegramApiException exception) {
            logger.error("Не удалось отправить сообщение!", exception);
        }

        return null;
    }

    public Message sendMessage(Long chatId, String text) {
        return sendMessage(chatId, text, true);
    }
}
