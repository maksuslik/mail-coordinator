package me.maksuslik.coordinator.command;

import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.user.UserService;
import me.maksuslik.coordinator.user.state.EmailEnteringState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class StartCommand implements IBotCommand {
    @Autowired
    private Bot bot;

    @Autowired
    private UserService userService;

    @Value("${message.start}")
    private String message;

    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public List<String> getArgs() {
        return List.of("recipient", "content");
    }

    @Override
    public void execute(Update update) {
        bot.sendMessage(update.getMessage().getChatId(), message);
        userService.setState(update.getMessage().getFrom().getId(), EmailEnteringState.class);
    }
}
