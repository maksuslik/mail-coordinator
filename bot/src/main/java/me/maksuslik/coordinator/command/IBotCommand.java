package me.maksuslik.coordinator.command;

import me.maksuslik.coordinator.bot.Bot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public interface IBotCommand {
    String getName();

    List<String> getArgs();

    void execute(Update update);

    default void sendMessage(Bot bot, Long chatId, String text) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException exception) {
            bot.logger.error("Не удалось отправить сообщение!", exception);
        }
    }
}
