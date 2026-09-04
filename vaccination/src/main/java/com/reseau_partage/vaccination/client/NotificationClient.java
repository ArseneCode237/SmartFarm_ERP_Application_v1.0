package com.reseau_partage.vaccination.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationClient {
    private final RestTemplate restTemplate;
    @Value("${services.notifications.url:}") private String notificationsUrl;

    public NotificationClient(RestTemplate restTemplate) { this.restTemplate = restTemplate; }

    public void sendAlert(String type, String priorite, String message, Long sujetId) {
        if (notificationsUrl == null || notificationsUrl.isBlank()) return;
        try {
            restTemplate.postForObject(notificationsUrl + "/api/notifications/alertes",
                    Map.of("type", type, "priorite", priorite, "message", message, "sujetId", sujetId), Map.class);
        } catch (RuntimeException ignored) {
            // Notifications remain best-effort and must not block a vaccination record.
        }
    }
}
