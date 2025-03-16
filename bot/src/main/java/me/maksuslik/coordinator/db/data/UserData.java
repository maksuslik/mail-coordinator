package me.maksuslik.coordinator.db.data;

import com.google.api.services.gmail.model.WatchRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.maksuslik.coordinator.MailCoordinator;

import java.math.BigInteger;
import java.util.Collections;
import java.util.UUID;

@Entity(name = "users")
@Table(name = "users")
@AllArgsConstructor
@Getter
public class UserData {
    @Id
    private Long userId;

    private String id;

    private Long chatId;

    private String email;

    @Setter
    private BigInteger historyId;

    @Setter
    private Long expirationTime;

    @Setter
    private boolean isEnabledNotifications;

    public UserData() {
        this(0L, UUID.randomUUID().toString(), 0L, "", BigInteger.ZERO, 0L, false);
    }

    @SneakyThrows
    public void updateWatch() {
        WatchRequest request = new WatchRequest();
        request.setTopicName("projects/tbot-307612/topics/BotTopic");
        request.setLabelIds(Collections.singletonList("INBOX"));
        request.setLabelFilterAction("INCLUDE");

        var result = MailCoordinator.INSTANCE.getService(userId, UUID.fromString(this.getId())).users().watch(this.getEmail(), request).execute();
        this.setHistoryId(result.getHistoryId());
        this.setExpirationTime(result.getExpiration());
    }
}
