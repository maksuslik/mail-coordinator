package me.maksuslik.coordinator.handler;

import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.command.CommandManager;
import me.maksuslik.coordinator.command.IBotCommand;
import me.maksuslik.coordinator.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class MessageHandler implements IHandler {
    @Autowired
    private Bot bot;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserService userService;

    @Autowired
    private CommandManager commandManager;

    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        Long userId = update.getMessage().getFrom().getId();

        if(userService.getState(userId) != null) {
            userService.getState(userId).execute(update);
            return;
        }

        if(!update.getMessage().getText().startsWith("/"))
            return;

        Long chatId = update.getMessage().getChatId();
        String[] text = update.getMessage().getText().split(" ");
        String command = text[0];

        Optional<IBotCommand> foundCommand = commandManager.getRegisteredCommands()
                .stream()
                .filter(predicate -> predicate.getName().equals(command))
                .findFirst()
                .or(() -> {
                    bot.sendMessage(chatId, "Неизвестная команда!");
                    return Optional.empty();
                });

        foundCommand.ifPresent(cmd -> cmd.execute(update));
    }
}
