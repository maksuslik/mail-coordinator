package me.maksuslik.coordinator.command;

import me.maksuslik.coordinator.bot.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class StartCommand implements IBotCommand {
    @Autowired
    private Bot bot;

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
        this.sendMessage(bot, update.getMessage().getChatId(), message);
    }
}
