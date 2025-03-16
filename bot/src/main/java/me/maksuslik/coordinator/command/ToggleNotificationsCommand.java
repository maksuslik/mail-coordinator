package me.maksuslik.coordinator.command;

import lombok.SneakyThrows;
import me.maksuslik.coordinator.MailCoordinator;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.db.data.UserData;
import me.maksuslik.coordinator.db.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

@Component
public class ToggleNotificationsCommand implements IBotCommand {
    @Autowired
    private Bot bot;

    @Autowired
    private UserRepo userRepo;

    @Override
    public String getName() {
        return "/toggle";
    }

    @SneakyThrows
    @Override
    public void execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        UserData user = userRepo.findById(userId).orElseThrow();
        boolean enabled = user.isEnabledNotifications();

        if (!enabled) {
            user.updateWatch();
            userRepo.save(user);
            bot.sendMessage(chatId, "Уведомления включены");
        } else {
            MailCoordinator.INSTANCE.getService(userId, UUID.fromString(user.getId())).users().stop(user.getEmail()).execute();
            bot.sendMessage(chatId, "Уведомления отключены");
        }

        user.setEnabledNotifications(!user.isEnabledNotifications());
        userRepo.save(user);
    }
}
