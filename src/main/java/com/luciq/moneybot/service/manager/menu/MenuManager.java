package com.luciq.moneybot.service.manager.menu;

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

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuManager extends AbstractManager {

    final AnswerMethodFactory answerMethodFactory;

    final KeyboardFactory keyboardFactory;

    static final String MENU_TEXT = """
            👋 Привет! Это MoneyBot - твой личный финансовый помощник. \s
            Здесь ты можешь следить за доходами и расходами, устанавливать лимиты и управлять категориями. \s
            """;


    @Autowired
    public MenuManager(AnswerMethodFactory answerMethodFactory,
                       KeyboardFactory keyboardFactory) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
    }


    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        return answerMethodFactory.getSendMessage(
                chatId,
                MENU_TEXT,
                keyboardFactory.generateInlineKeyboard(
                        List.of("Лич. Кабинет", "Категории", "Помощь", "Обратная связь", "Новая операция"),
                        List.of(2, 2, 1),
                        List.of("account", "category", "help", "feedback", "transactions")

                )
        );
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                MENU_TEXT,
                keyboardFactory.generateInlineKeyboard(
                        List.of("Лич. Кабинет", "Категории", "Помощь", "Обратная связь", "Новая операция"),
                        List.of(3, 2, 1),
                        List.of("account", "category", "help", "feedback", "transactions")

                )
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

}
