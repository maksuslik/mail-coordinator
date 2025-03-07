package me.maksuslik.coordinator.user.state;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import lombok.SneakyThrows;
import me.maksuslik.coordinator.MailCoordinator;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class EmailEnteringState implements IUserState {
    @Autowired
    private Bot bot;

    @Autowired
    private UserService userService;

    @SneakyThrows
    @Override
    public void execute(Update update) {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleAuthorizationCodeFlow flow = MailCoordinator.INSTANCE.getAuthorizationCodeFlow(httpTransport);
        bot.sendMessage(update.getMessage().getChatId(), flow.newAuthorizationUrl().setRedirectUri("http://localhost:8888/Callback").toURI().toString(), false);
        MailCoordinator.INSTANCE.getCredentials(flow, update.getMessage().getFrom().getId()).whenComplete((credential, exception) -> {
            if(exception != null)
                throw new RuntimeException(exception.getMessage());

            bot.sendMessage(update.getMessage().getChatId(), "Авторизован!");
            userService.setIdleState(update.getMessage().getFrom().getId());

            MailCoordinator.INSTANCE.getService(credential);
        });
    }
}
