package me.maksuslik.coordinator.service.command;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import me.maksuslik.coordinator.handler.command.BotCommandHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Data
@Service
public class CommandServiceImpl implements CommandService {

    ApplicationContext context;
    Set<BotCommandHandler> registeredCommands = new HashSet<>();

    @Override
    public void registerCommand(Class<? extends BotCommandHandler> command) {
        registeredCommands.add(context.getBean(command));
    }
}
