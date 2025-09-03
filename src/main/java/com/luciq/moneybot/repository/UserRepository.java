package com.luciq.moneybot.repository;

import com.luciq.moneybot.entity.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByChatId(@NonNull Long id);

    Object findByChatId(Long chatId);
}
