package me.maksuslik.coordinator.message;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.gmail.model.Message;
import lombok.SneakyThrows;
import me.maksuslik.coordinator.MailCoordinator;
import org.apache.commons.codec.binary.Base64;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.UUID;

public class MessageSender {
    /**
     * Готовый метод для отправки сообщений
     *
     * @param fromEmailAddress Адрес электронной почты отправителя
     * @param toEmailAddress   Адрес электронной почты получателя
     * @return Модель сообщения с информацией о нём
     */
    @SneakyThrows
    public static Message sendEmail(String fromEmailAddress, String toEmailAddress, String subject, String body, Long userId, UUID id) {
        // Шифруем MIME сообщение
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmailAddress));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(toEmailAddress));
        email.setSubject(subject);
        email.setText(body);

        // И формируем из него Gmail сообщение
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        // Пытаемся отправить сообщение, если не получилось - выбрасываем ошибку
        try {
            message = MailCoordinator.INSTANCE.getService(userId, id).users().messages().send(fromEmailAddress, message).execute();
            System.out.println(message.toPrettyString());
            return message;
        } catch (GoogleJsonResponseException exception) {
            GoogleJsonError error = exception.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Не удалось отправить сообщение: " + exception.getDetails());
            } else {
                throw exception;
            }
        }
        return null;
    }
}