package me.maksuslik.coordinator.command;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface IBotCommand {
    String getName();

    List<String> getArgs();

    void execute(Update update);
}
