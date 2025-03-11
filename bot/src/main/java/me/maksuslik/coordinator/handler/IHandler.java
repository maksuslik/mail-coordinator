package me.maksuslik.coordinator.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IHandler {
    void handle(Update update);
}
