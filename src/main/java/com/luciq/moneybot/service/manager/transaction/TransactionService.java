package com.luciq.moneybot.service.manager.transaction;

import com.luciq.moneybot.entity.Transaction;
import com.luciq.moneybot.entity.User;
import com.luciq.moneybot.repository.TransactionRepository;
import com.luciq.moneybot.repository.UserRepository;
import com.luciq.moneybot.service.Parser;
import com.luciq.moneybot.service.data.Action;
import com.luciq.moneybot.service.data.TransactionType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionService {

    final TransactionRepository transactionRepository;

    final UserRepository userRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public boolean createTransaction(User user, String text) {
        List<Object> transactionParts = Parser.parseTransaction(text);
        if (transactionParts.isEmpty()) {
            return false;
        }
        Transaction transaction = Transaction.builder()
                .amount(new BigDecimal(String.valueOf(transactionParts.get(0))))
                .category(transactionParts.get(1).toString())
                .createdAt(LocalDateTime.now())
                .user(user)
                .type(TransactionType.EXPENSE)
                .build();

        user.setAction(Action.FREE);
        userRepository.save(user);
        transactionRepository.save(transaction);
        return true;
    }

}
