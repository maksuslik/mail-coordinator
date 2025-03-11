package me.maksuslik.coordinator.user.state;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class WaitingState implements IUserState {
    @Override
    public void execute(Update update) {

    }
}
