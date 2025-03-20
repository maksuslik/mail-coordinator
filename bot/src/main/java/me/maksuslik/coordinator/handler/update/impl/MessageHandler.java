package me.maksuslik.coordinator.handler.update.impl;

import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.handler.command.BotCommandHandler;
import me.maksuslik.coordinator.handler.update.UpdateHandler;
import me.maksuslik.coordinator.service.command.CommandServiceImpl;
import me.maksuslik.coordinator.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.Optional;

@Component
public class MessageHandler implements UpdateHandler {
    @Autowired
    private Bot bot;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private CommandServiceImpl commandService;

    public void handle(Update update) {
        Long userId = update.getMessage().getFrom().getId();

        if (userService.getState(userId) != null) {
            userService.getState(userId).execute(update);
            return;
        }

        if (!update.getMessage().getText().startsWith("/"))
            return;

        Long chatId = update.getMessage().getChatId();
        String[] text = update.getMessage().getText().split(" ");
        String command = text[0];

        Optional<BotCommandHandler> foundCommand = commandService.getRegisteredCommands()
                .stream()
                .filter(predicate -> predicate.getName().equals(command))
                .findFirst()
                .or(() -> {
                    bot.sendMessage(chatId, "Неизвестная команда!");
                    return Optional.empty();
                });

        foundCommand.ifPresent(cmd -> {
            try {
                cmd.execute(update);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
