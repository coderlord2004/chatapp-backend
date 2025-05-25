package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User sender;

    @ManyToOne(optional = false)
    private ChatRoom room;

    @Nullable
    @ManyToOne
    private ChatMessage replyTo;

    @Nullable
    private String message;

    @Nullable
    private Timestamp lastEdit;

    @CreationTimestamp
    private Timestamp sentOn;

    @OneToMany
    private List<Attachment> attachments;

    @Column(nullable = false)
    private Status status;

    public enum Status {
        NORMAL, EDITED, RECALLED
    }

    private boolean isStatusValid() {

        if (status == Status.RECALLED) {
            return message == null
                && lastEdit == null
                && attachments.isEmpty();
        }

        if (status == Status.EDITED && lastEdit == null) {
            return false;
        }

        return message != null;
    }

    @PrePersist
    private void checkStatus() {
        if (!this.isStatusValid()) {
            throw new IllegalStateException("message and status state are not valid");
        }
    }

    public boolean isRecalled() {
        return status == Status.RECALLED;
    }
}
