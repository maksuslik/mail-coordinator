package me.maksuslik.coordinator.message;

import com.google.api.services.gmail.model.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EmailMessage {
    String from;
    String to;
    String subject;
    String body;

    public Message send(Long userId, UUID id) {
        return MessageSender.sendEmail(from, to, subject, body, userId, id);
    }
}
