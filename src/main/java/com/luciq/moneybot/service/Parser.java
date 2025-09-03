package com.luciq.moneybot.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Parser {

    public static Pattern transactionPattern = Pattern.compile("^\\s*([\\d .,]+)\\s+(.+)$");

    public static List<Object> parseTransaction(String input) {
        Matcher matcher = transactionPattern.matcher(input.trim());
        if (matcher.find()) {
            String rowAmount = matcher.group(1)
                    .replace(",", ".")
                    .replace(" ", "");
            String category = matcher.group(2);
            BigDecimal amount = new BigDecimal(rowAmount);
            return List.of(amount, category);
        }
        return List.of();
    }

    public static BigDecimal parseLimit(String input) {
        String cleaned = input.trim();
        cleaned = cleaned.replace(" ", "");
        if (cleaned.contains(",") && cleaned.contains(".")) {
            cleaned = cleaned.replace(".", "").replace(",", ".");
        }
        else if (cleaned.contains(",")) {
            cleaned = cleaned.replace(",", ".");
        }
        if (!cleaned.matches("^[0-9]*[.,]?[0-9]+$")) {
            throw new IllegalArgumentException("Лимит должен содержать только число без букв");
        }

        return new BigDecimal(cleaned);
    }

}
