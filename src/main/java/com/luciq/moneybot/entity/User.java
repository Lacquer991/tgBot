package com.luciq.moneybot.entity;

import com.luciq.moneybot.service.data.Action;
import com.luciq.moneybot.service.data.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class User {

    @Id
    @Column(name = "chat_id", nullable = false, unique = true)
    Long chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    Action action;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "user_limit")
    BigDecimal limit;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Transaction> transactions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


}
