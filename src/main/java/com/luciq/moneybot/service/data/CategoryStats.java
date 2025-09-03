package com.luciq.moneybot.service.data;

import java.math.BigDecimal;

public record CategoryStats(
        String category,
        BigDecimal amount
) {

}
