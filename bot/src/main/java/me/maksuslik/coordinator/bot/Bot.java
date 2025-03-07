package me.maksuslik.coordinator.bot;

import me.maksuslik.coordinator.command.CommandManager;
import me.maksuslik.coordinator.command.SendEmailCommand;
import me.maksuslik.coordinator.command.StartCommand;
import me.maksuslik.coordinator.command.TestCommand;
import me.maksuslik.coordinator.handler.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
public class Bot extends TelegramLongPollingBot {
    public final Logger logger = LoggerFactory.getLogger(Bot.class);

    @Autowired
    public CommandManager commandManager;

    @Lazy
    @Autowired
    public MessageHandler messageHandler;

    @Value("${bot.username}")
    private String BOT_USERNAME;

    @Value("${bot.token}")
    private String BOT_TOKEN;

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public void onRegister() {
        commandManager.registerCommand(StartCommand.class);
        commandManager.registerCommand(TestCommand.class);
        commandManager.registerCommand(SendEmailCommand.class);
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            messageHandler.handle(update);
        } catch (MessagingException | GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        /*if(!update.hasMessage() || !update.getMessage().hasText())
            return;

        if(update.getMessage().getText().equals("/start")) {
            sendMessage(update.getMessage().getChatId(), message);
            return;
        }

        System.out.println(update.getMessage().getText());*/
    }

    public Message sendMessage(Long chatId, String text, boolean useMarkdown) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);
        if(useMarkdown)
            sendMessage.setParseMode(ParseMode.MARKDOWN);

        try {
            return execute(sendMessage);
        } catch (TelegramApiException exception) {
            logger.error("Не удалось отправить сообщение!", exception);
        }

        return null;
    }

    public Message sendMessage(Long chatId, String text) {
        return sendMessage(chatId, text, true);
    }

    /*private void sendMessage(Long chatId, String text) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);

        try {
            this.execute(sendMessage);
        } catch (TelegramApiException exception) {
            logger.error("Не удалось отправить сообщение!", exception);
        }
    }*/

    /*public static void main(String... args) throws IOException, GeneralSecurityException, MessagingException {
        MailCoordinator mailCoordinator = new MailCoordinator();

        // Build a new authorized API client service.
        Scanner scanner = new Scanner(System.in);
        String email = scanner.nextLine();
        boolean isNewUser = scanner.nextBoolean();
        Gmail service = mailCoordinator.getService(isNewUser);

        // Print the labels in the user's account.
        String user = email;
        ListLabelsResponse listResponse = service.users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.isEmpty()) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Label label : labels) {
                System.out.printf("- %s\n", label.getName());
            }
        }

        *//*ListMessagesResponse result = service.users().messages().list(user).setLabelIds(Collections.singletonList("INBOX")).setMaxResults(1L).execute();
        System.out.println(result.getMessages().getFirst());*//*

        MessageSender messageSender = new MessageSender(mailCoordinator);

        Message message = messageSender.sendEmail(user, "kuzminm402@gmail.com");
        System.out.println(message.getPayload());
    }*/
}
