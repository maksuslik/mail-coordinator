package me.maksuslik.coordinator.thread;

import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.message.EmailMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SendEmailThread extends Thread {
    @Override
    public void run() {
        super.run();
        while (true) {
            System.out.println("thread 2");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendEmail(Bot bot, Update update, EmailMessage message) {
        Message sendingMessage = bot.sendMessage(update.getMessage().getChatId(), "✍\uFE0F _Сообщение отправляется..._");

        System.out.println("sending...");
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
    }
}
