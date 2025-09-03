package com.luciq.moneybot.service.manager.account;

import static com.luciq.moneybot.service.data.CallBackData.*;

import com.luciq.moneybot.service.manager.AbstractManager;
import com.luciq.moneybot.service.factory.AnswerMethodFactory;
import com.luciq.moneybot.service.factory.KeyboardFactory;
import com.luciq.moneybot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.math.BigDecimal;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountManager extends AbstractManager {

    final AnswerMethodFactory answerMethodFactory;

    final KeyboardFactory keyboardFactory;

    final AccountService accountService;

    static final String ACCOUNT_TEXT = """
            👋 Добро пожаловать в личный кабинет!
            
            📅 Траты за текущий месяц: %s ₽ / %s ₽
            
            💸 Последняя операция:
               • Сумма: %s ₽
               • Категория: %s
            
            📊 Трат всего: %s ₽
            """;


    @Autowired
    public AccountManager(AnswerMethodFactory answerMethodFactory,
                          KeyboardFactory keyboardFactory,
                          AccountService accountService) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.accountService = accountService;
    }


    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return buildAccountView(message.getChatId(), false, null);

    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return buildAccountView(callbackQuery.getMessage().getChatId(), true, callbackQuery);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    private BotApiMethod<?> buildAccountView(Long chatId, boolean isEdit, CallbackQuery callbackQuery) {
        var lastTransaction = accountService.getLastTransaction(chatId);
        BigDecimal lastAmount = lastTransaction.amount();
        String lastCategory = lastTransaction.category();

        String messageText = String.format(
                ACCOUNT_TEXT,
                formatAmount(accountService.getExpensesPerMonth(chatId)),
                formatAmount(accountService.getUserLimit(chatId)),
                formatAmount(lastAmount),
                lastCategory,
                accountService.getTotalExpenses(chatId)
        );

        var keyboard = keyboardFactory.generateInlineKeyboard(
                List.of("Категории", "Новая операция", "Настройки", "Назад"),
                List.of(2, 1, 1),
                List.of(CATEGORY, TRANSACTIONS, SETTINGS, BACK)
        );

        return isEdit
                ? answerMethodFactory.getEditMessageText(callbackQuery, messageText, keyboard)
                : answerMethodFactory.getSendMessage(chatId, messageText, keyboard);
    }

    private String formatAmount(BigDecimal amount) {
        return String.format("%,.2f", amount)
                .replace(",", ".");
    }
}
