package com.group4.chatapp.models;

import com.group4.chatapp.models.Enum.NotificationType;
import com.group4.chatapp.models.Enum.TargetType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @ManyToOne(optional = false)
    private User sender;

    @ManyToOne(optional = false)
    private User receiver;

    @CreationTimestamp
    private Timestamp sentOn;

    @Builder.Default
    private Boolean isRead = false;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private NotificationType type;

    private Long targetId;
    private TargetType targetType;

}
