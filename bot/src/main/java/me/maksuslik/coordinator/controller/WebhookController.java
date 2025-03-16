package me.maksuslik.coordinator.controller;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.History;
import com.google.api.services.gmail.model.ListHistoryResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import me.maksuslik.coordinator.MailCoordinator;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.data.DecodedMessage;
import me.maksuslik.coordinator.data.NotificationRequest;
import me.maksuslik.coordinator.db.data.UserData;
import me.maksuslik.coordinator.db.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping
public class WebhookController {
    @Autowired
    private Bot bot;

    @Autowired
    private UserRepo userRepo;

    @Value("${message.email_received}")
    private String messageReceived;

    @SneakyThrows
    @PostMapping("/webhook/gmail")
    public ResponseEntity<Map<String, String>> webhook(@RequestBody NotificationRequest request) {
        String decoded = new String(Base64.getDecoder().decode(request.message().data()));

        DecodedMessage decodedMessage = new Gson().fromJson(decoded, DecodedMessage.class);
        UserData userData = userRepo.findByEmail(decodedMessage.getEmailAddress()).orElseThrow();

        Gmail.Users.History.List list = MailCoordinator.INSTANCE.getService(userData.getUserId(), UUID.fromString(userData.getId())).users().history().list(decodedMessage.getEmailAddress());
        list.setMaxResults(1L);
        list.setStartHistoryId(userData.getHistoryId());
        ListHistoryResponse response = list.execute();
        System.out.println(response);

        if (response.getHistory() == null)
            return new ResponseEntity<>(Map.of("status", "ok"), HttpStatus.OK);

        Optional<History> history = response.getHistory().stream().findFirst();
        if (history.isEmpty())
            return new ResponseEntity<>(Map.of("status", "ok"), HttpStatus.OK);

        String responseMessage = history.get().getMessages().stream().findFirst().orElseThrow().getId();

        var message = MailCoordinator.INSTANCE.getService(userData.getUserId(), UUID.fromString(userData.getId())).users().messages().get(decodedMessage.getEmailAddress(), responseMessage);
        var executed = message.execute();

        String content = getContent(executed);
        System.out.println(content);
        String sender = findHeader(executed, "From");
        String subject = findHeader(executed, "Subject");

        bot.sendMessage(userData.getChatId(), String.format(messageReceived, sender, subject, content), false);

        userData.setHistoryId(decodedMessage.getHistoryId());
        userRepo.save(userData);

        return new ResponseEntity<>(Map.of("status", "ok"), HttpStatus.OK);
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return new ResponseEntity<>(Map.of("status", "ok"), HttpStatus.OK);
    }

    private String findHeader(Message message, String key) {
        return message.getPayload().getHeaders().stream()
                .filter(header -> header.getName().equals(key))
                .findFirst()
                .orElseThrow()
                .getValue();
    }

    public String getContent(Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        getPlainTextFromMessageParts(message.getPayload().getParts(), stringBuilder);
        byte[] bodyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(stringBuilder.toString());
        return new String(bodyBytes, StandardCharsets.UTF_8);
    }

    private void getPlainTextFromMessageParts(List<MessagePart> messageParts, StringBuilder stringBuilder) {
        for (MessagePart messagePart : messageParts) {
            if (messagePart.getMimeType().equals("text/plain")) {
                stringBuilder.append(messagePart.getBody().getData());
            }

            if (messagePart.getParts() != null) {
                getPlainTextFromMessageParts(messagePart.getParts(), stringBuilder);
            }
        }
    }
}