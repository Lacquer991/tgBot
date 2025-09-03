package com.luciq.moneybot.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "navigation_history")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class UserNavigation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "chat_id", nullable = false, unique = true)
    Long userId;

    @Column(name = "current_menu")
    String currentMenu;

    @Column(name = "previous_menu")
    String previousMenu;
}
