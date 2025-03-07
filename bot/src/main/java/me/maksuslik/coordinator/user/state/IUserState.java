package me.maksuslik.coordinator.user.state;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IUserState {
    void execute(Update update);
}
