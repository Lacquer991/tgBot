package com.luciq.moneybot.service.manager.settings;

import com.luciq.moneybot.entity.User;
import com.luciq.moneybot.repository.TransactionRepository;
import com.luciq.moneybot.repository.UserRepository;
import com.luciq.moneybot.service.Parser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettingsService {

    final UserRepository userRepository;

    final TransactionRepository transactionRepository;

    @Autowired
    public SettingsService(UserRepository userRepository,
                           TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public boolean setUserLimit(Long chatId, String limit) {
        User user = (User) userRepository.findByChatId(chatId);
        BigDecimal userLimit = Parser.parseLimit(limit);
        if (userLimit.equals(BigDecimal.ZERO)) {
            return false;
        }
        user.setLimit(userLimit);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public void deleteAllTransactions(Long chatId) {
        transactionRepository.deleteAllByUser_ChatId(chatId);
    }
}
