package me.maksuslik.coordinator.user.state;

import lombok.SneakyThrows;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.message.EmailMessage;
import me.maksuslik.coordinator.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MessageCompletionState implements IUserState {
    @Autowired
    private Bot bot;

    @Autowired
    private UserService userService;

    Map<Long, EmailMessage> messages = new HashMap<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @SneakyThrows
    @Override
    public void execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();

        if(update.getMessage().getText().equals("/cancel"))
            return;

        if(!messages.containsKey(userId)) {
            // TODO: get "from" from db
            String from = "maksuslik228@gmail.com";
            EmailMessage emailMessage = new EmailMessage();
            emailMessage.setFrom(from);
            messages.put(userId, emailMessage);
        }

        EmailMessage message = messages.get(userId);
        String messageText = update.getMessage().getText();

        if(message.getTo() == null) {
            message.setTo(messageText);
            bot.sendMessage(update.getMessage().getChatId(), "Укажите заголовок сообщения");
            return;
        }

        if(message.getSubject() == null) {
            message.setSubject(messageText);
            bot.sendMessage(update.getMessage().getChatId(), "Напишите сообщение");
            return;
        }

        if(message.getBody() == null) {
            message.setBody(messageText);
        }

        Message sendingMessage = bot.sendMessage(update.getMessage().getChatId(), "✍\uFE0F _Сообщение отправляется..._");

        System.out.println("sending...");
        userService.setIdleState(userId);
        messages.remove(userId);

        executorService.submit(() -> {
            message.send(update.getMessage().getFrom().getId());

            EditMessageText newMessage = new EditMessageText();
            System.out.println("message id: " + sendingMessage);
            newMessage.setMessageId(sendingMessage.getMessageId());
            newMessage.setChatId(update.getMessage().getChatId());
            newMessage.setText("✅ _Сообщение отправлено!_");
            newMessage.enableMarkdown(true);

            try {
                bot.execute(newMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
