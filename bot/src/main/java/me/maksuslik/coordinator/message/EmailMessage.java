package me.maksuslik.coordinator.message;

import com.google.api.services.gmail.model.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailMessage {
    String from;
    String to;
    String subject;
    String body;

    public Message send(Long userId) {
        return MessageSender.sendEmail(from, to, subject, body, userId);
    }
}
