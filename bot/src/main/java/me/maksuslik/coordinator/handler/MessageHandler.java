package me.maksuslik.coordinator.handler;

import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.command.IBotCommand;
import me.maksuslik.coordinator.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Component
public class MessageHandler {
    @Autowired
    private Bot bot;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserService userService;

    public void handle(Update update) throws MessagingException, GeneralSecurityException, IOException {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        Long userId = update.getMessage().getFrom().getId();

        if(userService.getState(userId) != null) {
            userService.getState(userId).execute(update);
        }

        if(!update.getMessage().getText().startsWith("/"))
            return;

        Long chatId = update.getMessage().getChatId();
        String[] text = update.getMessage().getText().split(" ");
        String command = text[0];

        Optional<IBotCommand> foundCommand = bot.commandManager.registeredCommands
                .stream()
                .filter(predicate -> predicate.getName().equals(command))
                .findFirst()
                .or(() -> {
                    sendMessage(chatId, "Неизвестная команда!");
                    return Optional.empty();
                });

        if (foundCommand.isEmpty())
            return;

        foundCommand.orElseThrow().execute(update);
    }

    private void sendMessage(Long chatId, String text) {
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
