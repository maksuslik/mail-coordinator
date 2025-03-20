package me.maksuslik.coordinator.service.email;

import com.google.api.services.gmail.model.Message;
import me.maksuslik.coordinator.data.EmailMessage;

import java.util.UUID;

public interface EmailService {

    Message send(EmailMessage message, Long userId, UUID id);

    Message sendEmail(String senderEmail, String targetEmail, String subject, String body, Long userId, UUID id);
}
