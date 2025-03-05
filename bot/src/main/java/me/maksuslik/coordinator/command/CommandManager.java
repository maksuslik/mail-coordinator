package me.maksuslik.coordinator.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CommandManager {
    @Autowired
    private ApplicationContext context;

    public Set<IBotCommand> registeredCommands = new HashSet<>();

    public void registerCommand(Class<? extends IBotCommand> command) {
        registeredCommands.add(context.getBean(command));
        System.out.println("size: " + registeredCommands.size());
    }
}
