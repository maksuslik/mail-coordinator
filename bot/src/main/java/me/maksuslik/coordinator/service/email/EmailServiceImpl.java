package me.maksuslik.coordinator.service.email;

import com.google.api.services.gmail.model.Message;
import me.maksuslik.coordinator.data.EmailMessage;
import me.maksuslik.coordinator.message.MessageSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public Message send(EmailMessage message, Long userId, UUID id) {
        return this.sendEmail(message.getFrom(), message.getTo(), message.getSubject(), message.getBody(), userId, id);
    }

    @Override
    public Message sendEmail(String senderEmail, String targetEmail, String subject, String body, Long userId, UUID id) {
        return MessageSender.sendEmail(senderEmail, targetEmail, subject, body, userId, id);
    }
}
