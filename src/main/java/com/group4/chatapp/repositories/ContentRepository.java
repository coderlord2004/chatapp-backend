package com.group4.chatapp.repositories;

import com.group4.chatapp.models.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ContentRepository extends JpaRepository<Content, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Content c SET c.totalReactions = c.totalReactions + 1 WHERE c.id = :id")
    void increaseReactions(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Content c SET c.totalReactions = c.totalReactions - 1 WHERE c.id = :id AND c.totalReactions > 0")
    void decreaseReactions(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Content c SET c.totalComments = c.totalComments + 1 WHERE c.id = :id")
    void increaseComments(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Content c SET c.totalComments = c.totalComments - 1 WHERE c.id = :id AND c.totalComments > 0")
    void decreaseComments(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Content c SET c.totalShares = c.totalShares + 1 WHERE c.id = :id")
    void increaseShares(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Content c SET c.totalShares = c.totalShares - 1 WHERE c.id = :id AND c.totalShares > 0")
    void decreaseShares(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Content c SET c.totalViews = c.totalViews + 1 WHERE c.id = :id")
    void increaseViews(@Param("id") Long id);
}
