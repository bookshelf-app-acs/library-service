package com.bookshelf.idp.libraryservice.service;

import com.bookshelf.idp.libraryservice.dto.NotificationRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Service
public class NotificationService {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url:http://localhost:8084}")
    private String notificationServiceUrl;

    public NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendNotification(UUID userId, String type, String message, UUID bookId) {
        try {
            NotificationRequestDto dto = new NotificationRequestDto(userId, type, message, bookId);
            restTemplate.postForObject(notificationServiceUrl + "/api/v1/notifications", dto, Void.class);
        } catch (Exception e) {
            System.out.println("Notification service unavailable: " + e.getMessage());
        }
    }
}