package com.pokemonreview.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long notificationId;

    private long workspaceId;
    private String title;
    private String topic;
    private String payload;
    private long userId;
    private long createTime;
    private long updatedTime;
}
