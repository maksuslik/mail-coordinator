package me.maksuslik.coordinator.handler.command;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface BotCommandHandler {
    String getName();

    void execute(Update update) throws IOException;
}
