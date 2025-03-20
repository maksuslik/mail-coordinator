package me.maksuslik.coordinator.handler.command.impl;

import lombok.Data;
import lombok.SneakyThrows;
import me.maksuslik.coordinator.MailCoordinator;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.entity.UserData;
import me.maksuslik.coordinator.handler.command.BotCommandHandler;
import me.maksuslik.coordinator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

@Component
@Data
public class ToggleNotificationsCommandHandler implements BotCommandHandler {
    @Lazy
    @Autowired
    private Bot bot;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String getName() {
        return "/toggle";
    }

    @SneakyThrows
    @Override
    public void execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        UserData user = userRepository.findById(userId).orElseThrow();
        boolean enabled = user.isEnabledNotifications();

        if (!enabled) {
            user.updateWatch();
            userRepository.save(user);
            bot.sendMessage(chatId, "Уведомления включены");
        } else {
            MailCoordinator.INSTANCE.getService(userId, UUID.fromString(user.getId())).users().stop(user.getEmail()).execute();
            bot.sendMessage(chatId, "Уведомления отключены");
        }

        user.setEnabledNotifications(!user.isEnabledNotifications());
        userRepository.save(user);
    }
}
