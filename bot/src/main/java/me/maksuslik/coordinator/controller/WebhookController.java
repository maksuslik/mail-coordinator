package me.maksuslik.coordinator.controller;

import me.maksuslik.coordinator.data.NotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping
public interface WebhookController {

    @PostMapping("/webhook/gmail")
    ResponseEntity<Map<String, String>> send(@RequestBody NotificationRequest request);

    @GetMapping("/ping")
    ResponseEntity<Map<String, String>> ping();
}