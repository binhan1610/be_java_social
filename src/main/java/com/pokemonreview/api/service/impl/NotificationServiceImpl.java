package com.pokemonreview.api.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.messaging.*;
import com.pokemonreview.api.dto.SaveNotiDto;
import com.pokemonreview.api.models.NotificationEntity;
import com.pokemonreview.api.models.ProfileEntity;
import com.pokemonreview.api.models.UserEntity;
import com.pokemonreview.api.repository.NotificationRepository;
import com.pokemonreview.api.repository.ProfileRepository;
import com.pokemonreview.api.repository.UserRepository;
import com.pokemonreview.api.service.NotificationService;
import com.pokemonreview.api.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private UserRepository userRepository;
    private TemplateService templateService;
    private NotificationRepository notificationRepository;
    private ProfileRepository profileRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,TemplateService templateService,UserRepository userRepository
    ,ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.templateService = templateService;
        this.notificationRepository = notificationRepository;
    }

    public void saveNoti(List<NotificationEntity> noti) {
         for(NotificationEntity notificationEntity:noti)
         {
             notificationRepository.save(notificationEntity);
         }
    }

    public String sendNotification(String title, String payload, String fcm_token) {
        UserEntity user = userRepository.findByFcmToken(fcm_token).orElse(null);
        if(user != null)
        {
            ProfileEntity profile = profileRepository.findById(user.getUserId()).orElse(null);
            HashMap<String,Object> model = new HashMap<>();
            model.put("title",title);
            model.put("payload",payload);
            try {
                JsonNode jsonNode = templateService.generateJsonFromTemplate("responseTitleNoti.ftl",model);
                JsonNode jsonNode1 = templateService.generateJsonFromTemplate("responsePayloadNoti.ftl",model);
                String resTitle = jsonNode.toString();
                String resPayload = jsonNode1.toString();
                Notification notification = Notification.builder()
                        .setTitle(resTitle)
                        .setBody(resPayload)
                        .setImage(profile != null? profile.getAvatar() : null)
                        .build();
                Message message = Message.builder()
                        .setToken(fcm_token)
                        .setNotification(notification)
                        .build();
                String response = FirebaseMessaging.getInstagitsend(message);
                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setTitle(resTitle);
                notificationEntity.setPayload(resPayload);
                notificationEntity.setUserId(user.getUserId());
                notificationRepository.save(notificationEntity);
                System.out.println("send noti success");
                return "send noti success";
            }
            catch (Exception e) {
                e.printStackTrace();
                return "send fail";
            }
        }
        else {
            throw new RuntimeException("user not found");
        }
    }

    public String sendNotificationMultiple(String title, String payload,List<String> list_fcm) {
        HashMap<String,Object> model = new HashMap<>();
        model.put("leng","en");
        model.put("title",title);
        model.put("payload",payload);
        try{
            JsonNode jsonNode = templateService.generateJsonFromTemplate("responseTitleNoti.ftl",model);
            JsonNode jsonNode1 = templateService.generateJsonFromTemplate("responsePayload.ftl",model);
            String resTitle= jsonNode.toString();
            String resPayload = jsonNode1.toString();
            Notification notification = Notification.builder()
                    .setTitle(resTitle)
                    .setBody(resPayload)
                    .build();
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(notification)
                    .addAllTokens(list_fcm)
                    .build();
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            return "Số tin nhắn gửi thành công "+response.getSuccessCount();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("send fail");
        }
    }

    public String sendNotificationMul(String title, String payload,List<String> list_fcm) {
        for(String fcm:list_fcm)
        {
            sendNotification(title,payload,fcm);
        }
        return "success";
    }

    public String registerTopicforUser(List<String> list_fcm,String topic) {
        try {
            TopicManagementResponse res  = FirebaseMessaging.getInstance().subscribeToTopic(
                    list_fcm,topic
            );
            return res.getSuccessCount() + " tokens were subscribed successfully";
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Subscription failed");
        }
    }

    public String unRegisterTopicforUser(List<String> list_fcm,String topic) {
        try {
            TopicManagementResponse res  = FirebaseMessaging.getInstance().unsubscribeFromTopic(list_fcm,topic);
            return res.getSuccessCount() + " tokens were unsubscribed successfully";
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Subscription failed");
        }
    }

    public String sendNotiForCondition(String condition,String title,String payload){
        HashMap<String,Object> model = new HashMap<>();
        model.put("leng","en");
        model.put("title",title);
        model.put("payload",payload);
        try {
            JsonNode jsonNode = templateService.generateJsonFromTemplate("responseTitleNoti.ftl",model);
            JsonNode jsonNode1 = templateService.generateJsonFromTemplate("responsePayloadNoti.ftl",model);
            String resTitle= jsonNode.toString();
            String resPayload = jsonNode1.toString();
            Notification notification = Notification.builder()
                    .setTitle(resTitle)
                    .setBody(resPayload)
                    .build();
            Message message = Message.builder()
                    .setNotification(notification)
                    .setCondition(condition)
                    .build();
            return message.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Subscription failed");
        }
    }


}
