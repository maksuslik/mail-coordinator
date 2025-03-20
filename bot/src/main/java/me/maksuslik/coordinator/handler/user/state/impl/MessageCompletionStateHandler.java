package me.maksuslik.coordinator.handler.user.state.impl;

import lombok.SneakyThrows;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.data.Confirmation;
import me.maksuslik.coordinator.data.EmailMessage;
import me.maksuslik.coordinator.entity.UserData;
import me.maksuslik.coordinator.handler.update.impl.ButtonHandler;
import me.maksuslik.coordinator.handler.user.state.UserStateHandler;
import me.maksuslik.coordinator.repository.UserRepository;
import me.maksuslik.coordinator.service.bot.BotServiceImpl;
import me.maksuslik.coordinator.service.email.EmailService;
import me.maksuslik.coordinator.service.email.validator.EmailValidatorService;
import me.maksuslik.coordinator.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MessageCompletionStateHandler implements UserStateHandler {
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    Map<Long, EmailMessage> messages = new HashMap<>();
    @Autowired
    private Bot bot;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private BotServiceImpl botService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ButtonHandler buttonHandler;
    @Autowired
    private EmailValidatorService emailValidatorService;
    @Autowired
    private EmailService emailService;
    @Value("${message.incorrect_email}")
    private String incorrectEmailMessage;
    @Value("${message.email_accept}")
    private String emailAcceptMessage;

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
            UserData userData = userRepository.findById(userId).orElseThrow();
            String from = userData.getEmail();
            EmailMessage emailMessage = new EmailMessage();
            emailMessage.setFrom(from);
            messages.put(userId, emailMessage);
        }

        EmailMessage message = messages.get(userId);
        String messageText = update.getMessage().getText();

        if (message.getTo() == null) {
            if (!this.emailValidatorService.isValid(messageText)) {
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

        var confirmation = Confirmation.builder()
                .chatId(chatId)
                .message(String.format(emailAcceptMessage, message.getTo(), message.getSubject(), message.getBody()))
                .onAccept((data) -> processMessage(chatId, userId, message))
                .onDeny((data) -> {
                    bot.sendMessage(chatId, "Отправка отменена");
                    userService.setIdleState(userId);
                })
                .build();

        botService.send(confirmation, bot, buttonHandler);
        messages.remove(userId);
    }

    private void processMessage(Long chatId, Long userId, EmailMessage message) {
        var sendingMessage = bot.sendMessage(chatId, "✍\uFE0F _Сообщение отправляется..._");

        userService.setIdleState(userId);

        executorService.submit(() -> {
            var userData = userRepository.findById(userId).orElseThrow();

            this.emailService.send(message, userId, UUID.fromString(userData.getId()));

            var newMessage = new EditMessageText("✅ _Сообщение отправлено!_");
            newMessage.setMessageId(sendingMessage.getMessageId());
            newMessage.setChatId(chatId);
            newMessage.enableMarkdown(true);

            try {
                bot.getClient().execute(newMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}