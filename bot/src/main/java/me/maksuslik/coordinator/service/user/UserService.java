package me.maksuslik.coordinator.service.user;

import me.maksuslik.coordinator.handler.user.state.UserStateHandler;

public interface UserService {

    void setState(Long userId, Class<? extends UserStateHandler> state);

    void setIdleState(Long userId);

    UserStateHandler getState(Long userId);
}
