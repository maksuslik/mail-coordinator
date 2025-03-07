package me.maksuslik.coordinator.command;

import me.maksuslik.coordinator.bot.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class TestCommand implements IBotCommand {
    @Autowired
    private Bot bot;

    @Override
    public String getName() {
        return "/test";
    }

    @Override
    public List<String> getArgs() {
        return List.of();
    }

    @Override
    public void execute(Update update) {
        bot.sendMessage(update.getMessage().getChatId(), "test");
    }
}