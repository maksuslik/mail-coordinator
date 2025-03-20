package me.maksuslik.coordinator.data;

public record NotificationRequest(
    MessageRequest message,
    String subscription
) {
}
