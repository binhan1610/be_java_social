package com.pokemonreview.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class NotificationEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long notificationId;

    @Column(name = "title")
    String title;

    @Column(name= "topic")
    String topic;

    @Column(name = "payload")
    String payload;

    @Column(name = "user_id")
    Long userId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Date updatedAt;
}
