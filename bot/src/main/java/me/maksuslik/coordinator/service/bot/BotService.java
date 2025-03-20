package me.maksuslik.coordinator.service.bot;

import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.data.Confirmation;
import me.maksuslik.coordinator.handler.update.impl.ButtonHandler;

public interface BotService {

    void send(Confirmation confirmation, Bot bot, ButtonHandler buttonHandler);
}
