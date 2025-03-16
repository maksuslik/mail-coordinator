package me.maksuslik.coordinator.data;

public record MessageRequest(
        String data,
        String messageId,
        String publishTime
) {
}
