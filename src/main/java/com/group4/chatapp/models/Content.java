package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_reactions")
    private Long totalReactions = 0L;

    @Column(name = "total_comments")
    private Long totalComments = 0L;

    @Column(name = "total_shares")
    private Long totalShares = 0L;

    public Content(Long totalReactions, Long totalComments, Long totalShares) {
        this.totalReactions = totalReactions;
        this.totalComments = totalComments;
        this.totalShares = totalShares;
    }
}
