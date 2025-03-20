package me.maksuslik.coordinator.handler.user.state.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import lombok.SneakyThrows;
import me.maksuslik.coordinator.MailCoordinator;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.data.Confirmation;
import me.maksuslik.coordinator.entity.UserData;
import me.maksuslik.coordinator.handler.update.impl.ButtonHandler;
import me.maksuslik.coordinator.handler.user.state.UserStateHandler;
import me.maksuslik.coordinator.repository.UserRepository;
import me.maksuslik.coordinator.service.bot.BotService;
import me.maksuslik.coordinator.service.email.validator.EmailValidatorService;
import me.maksuslik.coordinator.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigInteger;
import java.util.UUID;

@Service
public class EmailEnteringStateHandler implements UserStateHandler {
    @Autowired
    private Bot bot;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ButtonHandler buttonHandler;

    @Autowired
    private EmailValidatorService emailValidatorService;

    @Autowired
    private BotService botService;

    @Value("${message.incorrect_email}")
    private String incorrectEmailMessage;

    @SneakyThrows
    @Override
    public void execute(Update update) {
        String entered = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        if (!this.emailValidatorService.isValid(entered)) {
            bot.sendMessage(chatId, incorrectEmailMessage);
            return;
        }

        userService.setState(userId, WaitingStateHandler.class);

        var confirmation = Confirmation.builder()
                .chatId(chatId)
                .message("Ваш адрес электронной почты: " + entered)
                .onAccept((data) -> authorize(update, chatId))
                .onDeny((data) -> {
                    bot.sendMessage(chatId, "Введите адрес электронной почты");
                    userService.setState(userId, EmailEnteringStateHandler.class);
                })
                .build();
        this.botService.send(confirmation, bot, buttonHandler);
    }

    @SneakyThrows
    private void authorize(Update update, Long chatId) {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleAuthorizationCodeFlow flow = MailCoordinator.INSTANCE.getAuthorizationCodeFlow(httpTransport);
        bot.sendMessage(chatId, flow.newAuthorizationUrl().setRedirectUri("http://localhost:8888/Callback").toURI().toString(), false);

        Long userId = update.getMessage().getFrom().getId();

        UUID uuid = UUID.randomUUID();
        MailCoordinator.INSTANCE.getCredentials(flow, userId, uuid).whenComplete((credential, exception) -> {
            if (exception != null)
                throw new RuntimeException(exception.getMessage());

            bot.sendMessage(chatId, "Авторизован!");
            userService.setIdleState(update.getMessage().getFrom().getId());

            MailCoordinator.INSTANCE.getService(credential);

            UserData data = new UserData(update.getMessage().getFrom().getId(), uuid.toString(), chatId, update.getMessage().getText(), BigInteger.ZERO, 0L, false);
            userRepository.save(data);
        });
    }
}
