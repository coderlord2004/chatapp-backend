package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @Builder.Default
    private int frequency = 1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
