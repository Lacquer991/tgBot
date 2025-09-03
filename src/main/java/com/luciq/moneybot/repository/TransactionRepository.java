package com.luciq.moneybot.repository;

import com.luciq.moneybot.entity.Transaction;
import com.luciq.moneybot.service.data.CategoryStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByUser_ChatIdAndCreatedAtBetween(
            Long chatId,
            LocalDateTime createdAtAfter,
            LocalDateTime createdAtBefore
    );

    Transaction findTopByUser_ChatIdOrderByCreatedAtDesc(Long chatId);

    List<Transaction> findAllByUser_ChatId(Long chatId);

    @Query("SELECT t.category as category , SUM(t.amount) " +
           "FROM Transaction as t " +
           "WHERE t.user.chatId = :chatId " +
           "GROUP BY category ORDER BY SUM(t.amount) DESC ")
    List<CategoryStats> getTransactionByCategoryOrderByAmountDesc(@Param("chatId") Long chatId);

    @Transactional
    void deleteAllByUser_ChatId(Long chatId);
}
