package me.maksuslik.coordinator.service.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import me.maksuslik.coordinator.handler.user.state.UserStateHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Data
@Service
public class UserServiceImpl implements UserService {

    ApplicationContext context;

    // TODO: to repository
    private final Map<Long, UserStateHandler> states = new HashMap<>();

    @Override
    public void setState(Long userId, Class<? extends UserStateHandler> state) {
        states.put(userId, context.getBean(state));
    }

    @Override
    public void setIdleState(Long userId) {
        states.remove(userId);
    }

    @Override
    public UserStateHandler getState(Long userId) {
        return states.get(userId);
    }
}
