package com.luciq.moneybot.repository;

import com.luciq.moneybot.entity.UserNavigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserNavigationRepository extends JpaRepository<UserNavigation, UUID> {


    UserNavigation getUserNavigationByUserId(Long userId);
}
