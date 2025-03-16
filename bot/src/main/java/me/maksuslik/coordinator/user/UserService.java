package me.maksuslik.coordinator.user;

import me.maksuslik.coordinator.user.state.IUserState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private ApplicationContext context;

    private final Map<Long, IUserState> states = new HashMap<>();

    public void setState(Long userId, Class<? extends IUserState> state) {
        states.put(userId, context.getBean(state));
    }

    public void setIdleState(Long userId) {
        states.remove(userId);
    }

    public IUserState getState(Long userId) {
        return states.get(userId);
    }

    public boolean isState(Long userId, Class<? extends IUserState> state) {
        IUserState currentState = states.get(userId);
        if (currentState == null)
            return false;

        return currentState == context.getBean(state);
    }
}
