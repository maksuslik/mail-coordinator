package me.maksuslik.coordinator.handler.command.impl;

import lombok.Data;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.handler.command.BotCommandHandler;
import me.maksuslik.coordinator.handler.user.state.impl.MessageCompletionStateHandler;
import me.maksuslik.coordinator.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Data
public class SendEmailCommandHandler implements BotCommandHandler {
    @Lazy
    @Autowired
    private Bot bot;

    @Autowired
    private UserServiceImpl userService;

    @Value("${message.email}")
    private String message;

    @Override
    public String getName() {
        return "/send";
    }

    @Override
    public void execute(Update update) {
        bot.sendMessage(update.getMessage().getChatId(), message);
        userService.setState(update.getMessage().getFrom().getId(), MessageCompletionStateHandler.class);
    }
}
