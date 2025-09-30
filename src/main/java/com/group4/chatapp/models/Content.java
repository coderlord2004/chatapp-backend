package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "total_views")
    private Long totalViews = 0L;
}
