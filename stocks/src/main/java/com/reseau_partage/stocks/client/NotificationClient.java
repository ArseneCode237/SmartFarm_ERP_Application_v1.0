package com.reseau_partage.stocks.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${services.notifications.url}")
    private String notificationsUrl;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendAlert(String priorite, String message, Long articleId) {
        try {
            String url = notificationsUrl + "/api/notifications/alertes";
            Map<String, Object> body = Map.of(
                    "type", "STOCK_" + priorite.toUpperCase(),
                    "articleId", articleId,
                    "message", message
            );
            restTemplate.postForObject(url, body, Map.class);
        } catch (Exception e) {
            System.err.println("Notification service unavailable: " + e.getMessage());
        }
    }
}