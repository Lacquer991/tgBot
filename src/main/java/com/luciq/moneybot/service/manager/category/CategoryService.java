package com.luciq.moneybot.service.manager.category;

import com.luciq.moneybot.repository.TransactionRepository;
import com.luciq.moneybot.service.data.CategoryStats;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryService {

    final TransactionRepository transactionRepository;

    @Autowired
    public CategoryService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public String getAllCategories(Long chatId) {
        StringBuilder text = new StringBuilder("📊 Траты по категориям:\n\n");
        List<CategoryStats> categoryList = transactionRepository.getTransactionByCategoryOrderByAmountDesc(chatId);

        BigDecimal total = categoryList.stream()
                .map(CategoryStats::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        categoryList.forEach(stat -> {
            text.append("▫️ ")
                    .append(capitalize(stat.category()))
                    .append(":  ")
                    .append(formatAmount(stat.amount()))
                    .append(" ₽\n");
        });

        text.append("\n💰 Всего: ").append(formatAmount(total)).append(" ₽");
        return text.toString();
    }

    private String capitalize(String text) {
        if (text == null || text.isBlank()) return text;
        return text.toLowerCase().substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String formatAmount(BigDecimal amount) {
        return String.format("%,.2f", amount)
                .replace(",", ".");
    }
}
