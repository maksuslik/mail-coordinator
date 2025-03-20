package me.maksuslik.coordinator.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailMessage {

    String from;
    String to;
    String subject;
    String body;
}
