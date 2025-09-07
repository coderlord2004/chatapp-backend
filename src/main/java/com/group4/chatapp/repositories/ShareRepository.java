package com.group4.chatapp.repositories;

import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.models.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShareRepository extends JpaRepository<Share, Long> {
    @Query("""
            SELECT COUNT(s.id)
            FROM Share s
            WHERE s.targetId = ?1 AND s.targetType = ?2
            """)
    Long getTotalShares(Long targetId, TargetType targetType);
}
