package me.maksuslik.coordinator.user.state;

import lombok.SneakyThrows;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.db.data.UserData;
import me.maksuslik.coordinator.db.repo.UserRepo;
import me.maksuslik.coordinator.handler.ButtonHandler;
import me.maksuslik.coordinator.message.EmailMessage;
import me.maksuslik.coordinator.message.preset.Confirmation;
import me.maksuslik.coordinator.user.UserService;
import me.maksuslik.coordinator.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MessageCompletionState implements IUserState {
    @Autowired
    private Bot bot;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ButtonHandler buttonHandler;

    @Value("${message.incorrect_email}")
    private String incorrectEmailMessage;

    @Value("${message.email_accept}")
    private String emailAcceptMessage;

    Map<Long, EmailMessage> messages = new HashMap<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @SneakyThrows
    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        if (update.getMessage().getText().equals("/cancel")) {
            bot.sendMessage(chatId, "Ввод остановлен");
            messages.remove(userId);
            userService.setIdleState(userId);
            return;
        }

        if (!messages.containsKey(userId)) {
            UserData userData = userRepo.findById(userId).orElseThrow();
            String from = userData.getEmail();
            EmailMessage emailMessage = new EmailMessage();
            emailMessage.setFrom(from);
            messages.put(userId, emailMessage);
        }

        EmailMessage message = messages.get(userId);
        String messageText = update.getMessage().getText();

        if (message.getTo() == null) {
            if (!Validator.isValidEmail(messageText)) {
                bot.sendMessage(chatId, incorrectEmailMessage);
                return;
            }

            message.setTo(messageText);
            bot.sendMessage(chatId, "Укажите заголовок сообщения");
            return;
        }

        if (message.getSubject() == null) {
            message.setSubject(messageText.replaceAll("[\\r\\n]+", " "));
            bot.sendMessage(chatId, "Напишите сообщение");
            return;
        }

        if (message.getBody() == null) {
            message.setBody(messageText);
        }

        Confirmation confirmation = Confirmation.builder()
                .chatId(chatId)
                .message(String.format(emailAcceptMessage, message.getTo(), message.getSubject(), message.getBody()))
                .onAccept((data) -> processMessage(chatId, userId, message))
                .onDeny((data) -> {
                    bot.sendMessage(chatId, "Отправка отменена");
                    userService.setIdleState(userId);
                })
                .build();
        confirmation.send(bot, buttonHandler);
        messages.remove(userId);
    }

    private void processMessage(Long chatId, Long userId, EmailMessage message) {
        Message sendingMessage = bot.sendMessage(chatId, "✍\uFE0F _Сообщение отправляется..._");

        userService.setIdleState(userId);

        executorService.submit(() -> {
            UserData userData = userRepo.findById(userId).orElseThrow();
            message.send(userId, UUID.fromString(userData.getId()));

            EditMessageText newMessage = new EditMessageText();
            newMessage.setMessageId(sendingMessage.getMessageId());
            newMessage.setChatId(chatId);
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