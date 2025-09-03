package com.luciq.moneybot.entity;


import com.luciq.moneybot.service.data.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID id;

    @Column(name = "amount")
    BigDecimal amount;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    TransactionType type;

    @Column(name = "category")
    String category;


    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    User user;

    @Column(name = "createdAt")
    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
