package me.maksuslik.coordinator.command;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface IBotCommand {
    String getName();

    void execute(Update update) throws IOException;
}
