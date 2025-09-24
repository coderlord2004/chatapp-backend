package com.group4.chatapp.repositories;

import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.models.Post;
import com.group4.chatapp.models.Reaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    @Query("""
            SELECT r
            FROM Reaction r
            WHERE r.user.id = ?1 AND r.targetId = ?2 AND r.targetType = ?3
            """)
    Reaction findByUserIdAndTargetId(Long userId, Long targetId, TargetType targetType);

    @Query("""
            SELECT r.reactionType, COUNT(r.reactionType)
            FROM Reaction r
            WHERE r.targetId = ?1 AND r.targetType = ?2
            GROUP BY r.reactionType
            ORDER BY r.reactionType DESC
            """)
    List<Object[]> countByReactionType(Long targetId, TargetType targetType);

    @Query("""
            SELECT r.reactionType
            FROM Reaction r
            WHERE r.targetId = ?1 AND r.targetType = ?2
            GROUP BY r.reactionType
            ORDER BY COUNT(r.reactionType) DESC
            """)
    List<ReactionType> getTopReactionType(Long targetId, TargetType targetType, Pageable pageable);


}
