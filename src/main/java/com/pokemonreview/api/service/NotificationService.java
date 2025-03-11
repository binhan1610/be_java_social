package com.pokemonreview.api.service;

import com.google.firebase.messaging.BatchResponse;

import java.util.List;

public interface NotificationService {
    String sendNotification(String title,String payload, String fcm_token);
    String sendNotificationMultiple(String title, String payload, List<String> fcm_tokens);
    String sendNotificationMul(String title,String payload,List<String> fcm_tokens);
}
