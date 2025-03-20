package me.maksuslik.coordinator.handler.user.state.impl;

import me.maksuslik.coordinator.handler.user.state.UserStateHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class WaitingStateHandler implements UserStateHandler {
    @Override
    public void execute(Update update) {

    }
}
