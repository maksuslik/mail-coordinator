package me.maksuslik.castles.message;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import me.maksuslik.castles.Main;
import org.apache.commons.codec.binary.Base64;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

public class MessageSender {
    private final Main main;

    public MessageSender(Main main) {
        this.main = main;
    }

    /**
     * Готовый метод для отправки сообщений
     *
     * @param fromEmailAddress Адрес электронной почты отправителя
     * @param toEmailAddress   Адрес электронной почты получателя
     * @return Модель сообщения с информацией о нём
     */
    public Message sendEmail(String fromEmailAddress, String toEmailAddress) throws MessagingException, IOException, GeneralSecurityException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Создаём новый клиент Gmail API
        Gmail service = new Gmail.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                main.getCredentials(httpTransport, false))
                .setApplicationName("Gmail samples")
                .build();

        // Создаём контент для будущего сообщения
        String messageSubject = "Test message";
        String bodyText = "lorem ipsum.";

        // Шифруем MIME (Multipurpose Internet Mail Extensions) сообщение
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmailAddress));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(toEmailAddress));
        email.setSubject(messageSubject);
        email.setText(bodyText);

        // И формируем из него Gmail сообщение
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        // Пытаемся отправить сообщение, если не получилось - выбрасываем ошибку
        try {
            message = service.users().messages().send("me", message).execute();
            System.out.println("Message id: " + message.getId());
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
