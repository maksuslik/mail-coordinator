package me.maksuslik.coordinator.handler.command.impl;

import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.entity.UserData;
import me.maksuslik.coordinator.handler.command.BotCommandHandler;
import me.maksuslik.coordinator.handler.user.state.impl.EmailEnteringStateHandler;
import me.maksuslik.coordinator.repository.UserRepository;
import me.maksuslik.coordinator.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class StartCommandHandler implements BotCommandHandler {
    @Lazy
    @Autowired
    private Bot bot;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Value("${message.start}")
    private String message;

    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        Optional<UserData> data = userRepository.findById(userId);
        if (userRepository.findById(userId).isPresent()) {
            bot.sendMessage(chatId, "Вы уже авторизованы!");
            return;
        }

        bot.sendMessage(update.getMessage().getChatId(), message);
        userService.setState(update.getMessage().getFrom().getId(), EmailEnteringStateHandler.class);
    }
}
