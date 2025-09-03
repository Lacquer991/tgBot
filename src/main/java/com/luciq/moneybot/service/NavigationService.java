package com.luciq.moneybot.service;

import com.luciq.moneybot.entity.UserNavigation;
import com.luciq.moneybot.repository.UserNavigationRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NavigationService {

    final UserNavigationRepository userNavigationRepository;


    @Autowired
    public NavigationService(UserNavigationRepository userNavigationRepository) {
        this.userNavigationRepository = userNavigationRepository;
    }

    public void saveOrUpdateUserNavigation(long chatId, String newMenu) {
        UserNavigation userNavigation = userNavigationRepository.getUserNavigationByUserId(chatId);
        if (userNavigation != null) {
            if (!newMenu.equals(userNavigation.getCurrentMenu()) && !newMenu.equals(userNavigation.getPreviousMenu())) {
                userNavigation.setPreviousMenu(userNavigation.getCurrentMenu());
                userNavigation.setCurrentMenu(newMenu);
            }
            else {
                userNavigation.setPreviousMenu("START");
                userNavigation.setCurrentMenu(newMenu);
            }
        } else {
            userNavigation = UserNavigation.builder()
                    .userId(chatId)
                    .currentMenu(newMenu)
                    .previousMenu(null)
                    .build();
        }
        userNavigationRepository.save(userNavigation);
    }

    public List<String> getUserNavigation(long chatId) {
        List<String> userNavigations = new ArrayList<>();
        UserNavigation nav = userNavigationRepository.getUserNavigationByUserId(chatId);
        userNavigations.add(nav.getCurrentMenu());
        userNavigations.add(nav.getPreviousMenu());
        return userNavigations;
    }


}