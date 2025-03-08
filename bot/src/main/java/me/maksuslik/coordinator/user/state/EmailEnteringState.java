package me.maksuslik.coordinator.user.state;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import lombok.SneakyThrows;
import me.maksuslik.coordinator.MailCoordinator;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.db.data.UserData;
import me.maksuslik.coordinator.db.repo.UserRepo;
import me.maksuslik.coordinator.user.UserService;
import me.maksuslik.coordinator.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

@Service
public class EmailEnteringState implements IUserState {
    @Autowired
    private Bot bot;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Value("${message.incorrect_email}")
    private String incorrectEmailMessage;

    @SneakyThrows
    @Override
    public void execute(Update update) {
        String entered = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();

        if (!Validator.isValidEmail(entered)) {
            bot.sendMessage(chatId, incorrectEmailMessage);
            return;
        }

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

            UserData data = new UserData(update.getMessage().getFrom().getId(), uuid.toString(), update.getMessage().getText());
            userRepo.save(data);
        });
    }
}
