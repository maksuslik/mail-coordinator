package me.maksuslik.coordinator.service.command;

import me.maksuslik.coordinator.handler.command.BotCommandHandler;

public interface CommandService {

    void registerCommand(Class<? extends BotCommandHandler> command);
}
