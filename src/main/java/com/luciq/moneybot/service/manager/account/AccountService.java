package com.luciq.moneybot.service.manager.account;

import com.luciq.moneybot.entity.Transaction;
import com.luciq.moneybot.entity.User;
import com.luciq.moneybot.repository.TransactionRepository;
import com.luciq.moneybot.repository.UserRepository;
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
public class AccountService {

    final TransactionRepository transactionRepository;
    private final UserRepository userRepository;


    @Autowired
    public AccountService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public BigDecimal getExpensesPerMonth(Long chatId) {
        LocalDateTime localDate = LocalDateTime.now();
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        BigDecimal expensesPerMonth = BigDecimal.ZERO;
        List<Transaction> monthTransactions = transactionRepository.
                findAllByUser_ChatIdAndCreatedAtBetween(chatId, startOfMonth, localDate);
        if (monthTransactions.isEmpty()) {
            return expensesPerMonth;
        }
        for (Transaction transaction : monthTransactions) {
            if (transaction.getType() == TransactionType.EXPENSE) {
                expensesPerMonth = expensesPerMonth.add(transaction.getAmount());
            } else expensesPerMonth = expensesPerMonth.subtract(transaction.getAmount());
        }
        return expensesPerMonth;
    }

    public LastTransactionDto getLastTransaction(Long chatId) {
        Transaction transaction = transactionRepository.findTopByUser_ChatIdOrderByCreatedAtDesc(chatId);
        if (transaction == null) {
            return new LastTransactionDto(BigDecimal.ZERO, "---");
        }
        if (transaction.getCategory() == null) {
            transaction.setCategory("---");
        }
        return new LastTransactionDto(transaction.getAmount(), transaction.getCategory());
    }

    public BigDecimal getTotalExpenses(Long chatId) {
        BigDecimal TotalExpenses = BigDecimal.ZERO;
        for (Transaction transaction : transactionRepository.findAllByUser_ChatId(chatId)) {
            if (transaction != null) {
                TotalExpenses = TotalExpenses.add(transaction.getAmount());
            }

        }
        return TotalExpenses;
    }

    public BigDecimal getUserLimit(Long chatId) {
        User user = (User) userRepository.findByChatId(chatId);
        BigDecimal limit = user.getLimit();
        return limit == null
                ? BigDecimal.ZERO
                : limit;
    }

    public record LastTransactionDto(BigDecimal amount, String category) {}
}
