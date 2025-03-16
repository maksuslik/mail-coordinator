package me.maksuslik.coordinator.command;

import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.user.UserService;
import me.maksuslik.coordinator.user.state.MessageCompletionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class SendEmailCommand implements IBotCommand {
    @Autowired
    private Bot bot;

    @Autowired
    private UserService userService;

    @Value("${message.email}")
    private String message;

    @Override
    public String getName() {
        return "/send";
    }

    @Override
    public void execute(Update update) {
        bot.sendMessage(update.getMessage().getChatId(), message);
        userService.setState(update.getMessage().getFrom().getId(), MessageCompletionState.class);
    }
}
