package me.maksuslik.coordinator.command;

import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.db.data.UserData;
import me.maksuslik.coordinator.db.repo.UserRepo;
import me.maksuslik.coordinator.user.UserService;
import me.maksuslik.coordinator.user.state.EmailEnteringState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Component
public class StartCommand implements IBotCommand {
    @Autowired
    private Bot bot;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Value("${message.start}")
    private String message;

    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public List<String> getArgs() {
        return List.of();
    }

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        Optional<UserData> data = userRepo.findById(userId);
        System.out.println("empty: " + data.isEmpty());
        System.out.println(userRepo.findAll());
        if(userRepo.findById(userId).isPresent()) {
            bot.sendMessage(chatId, "Вы уже авторизованы!");
            return;
        }

        bot.sendMessage(update.getMessage().getChatId(), message);
        userService.setState(update.getMessage().getFrom().getId(), EmailEnteringState.class);
    }
}
