package me.maksuslik.coordinator.handler.user.state;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserStateHandler {

    void execute(Update update);
}
