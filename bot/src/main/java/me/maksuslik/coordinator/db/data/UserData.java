package me.maksuslik.coordinator.db.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Entity(name = "users")
@Table(name = "users")
@AllArgsConstructor
@Getter
public class UserData {
    @Id
    @Column(name = "user_id")
    private Long userId;

    private String id;

    private String email;

    public UserData() {
        this(0L, UUID.randomUUID().toString(), "");
    }
}
