package com.luciq.moneybot.service.manager.settings;

import static com.luciq.moneybot.service.data.CallBackData.*;

import com.luciq.moneybot.entity.User;
import com.luciq.moneybot.repository.UserRepository;
import com.luciq.moneybot.service.data.Action;
import com.luciq.moneybot.service.data.CallBackData;
import com.luciq.moneybot.service.factory.AnswerMethodFactory;
import com.luciq.moneybot.service.factory.KeyboardFactory;
import com.luciq.moneybot.service.manager.AbstractManager;
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
public class SettingsManager extends AbstractManager {

    final AnswerMethodFactory answerMethodFactory;

    final KeyboardFactory keyboardFactory;

    final SettingsService settingsService;

    final UserRepository userRepository;

    static final String SETTINGS_TEXT = """
            ⚙️ Настройки
            
            Здесь вы можете управлять своим аккаунтом и историей операций. \s
            Вы можете:
            🗑 Удалить историю операций \s
            💰 Изменить лимит трат \s
            Все ваши сохранённые траты и операции будут удалены без возможности восстановления.
            """;

    @Autowired
    public SettingsManager(AnswerMethodFactory answerMethodFactory,
                           KeyboardFactory keyboardFactory,
                           SettingsService settingsService,
                           UserRepository userRepository) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.settingsService = settingsService;
        this.userRepository = userRepository;
    }


    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        return answerMethodFactory.getSendMessage(
                chatId,
                SETTINGS_TEXT,
                keyboardFactory.generateInlineKeyboard(
                        List.of("Удалить историю операций", "Изменить лимит трат", "Назад"),
                        List.of(1, 1, 1),
                        List.of(DELETE_HISTORY, SET_LIMIT, BACK)

                ));
    }


    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        switch (callbackQuery.getData()) {
            case CallBackData.SET_LIMIT -> {
                return askLimit(callbackQuery);
            }
            case CallBackData.DELETE_HISTORY -> {
                return deleteMonthTransactions(callbackQuery);
            }
            default -> {
                return answerMethodFactory.getEditMessageText(
                        callbackQuery,
                        SETTINGS_TEXT,
                        keyboardFactory.generateInlineKeyboard(
                                List.of("Удалить историю операций", "Изменить лимит трат", "Назад"),
                                List.of(1, 1, 1),
                                List.of(DELETE_HISTORY, SET_LIMIT, BACK)

                        ));
            }
        }
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return setLimit(message);
    }

    private BotApiMethod<?> askLimit(CallbackQuery callbackQuery) {
        User user = (User) userRepository.findByChatId(callbackQuery.getMessage().getChatId());
        user.setAction(Action.SENDING_LIMIT);
        userRepository.save(user);
        return answerMethodFactory.getEditMessageText(callbackQuery,
                """
                        Введите лимит трат
                        """,
                null);

    }

    private BotApiMethod<?> setLimit(Message message) {
        Long chatId = message.getChatId();
        if (settingsService.setUserLimit(chatId, message.getText())) {
            User user = (User) userRepository.findByChatId(chatId);
            user.setAction(Action.FREE);
            userRepository.save(user);
            return answerMethodFactory.getSendMessage(chatId,
                    """
                            Лимит успешно установлен
                            """,
                    keyboardFactory.generateInlineKeyboard(
                            List.of("Назад"),
                            List.of(1),
                            List.of(BACK_SETTINGS)
                    ));
        } else {
            return answerMethodFactory.getSendMessage(message.getChatId(),
                    """
                            Некорректная форма записи.
                            Лимит должен быть больше 0
                            """,
                    null);
        }
    }

    private BotApiMethod<?> deleteMonthTransactions(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        settingsService.deleteAllTransactions(chatId);
        return answerMethodFactory.getSendMessage(chatId,
                """
                        История операций успешно удалена
                        """,
                keyboardFactory.generateInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(BACK_SETTINGS)
                ));
    }
}
