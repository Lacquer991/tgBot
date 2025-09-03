package com.luciq.moneybot.service.manager.transaction;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class TransactionsManager extends AbstractManager {

    final AnswerMethodFactory answerMethodFactory;

    final KeyboardFactory keyboardFactory;

    final UserRepository userRepository;

    final TransactionService transactionService;


    @Autowired
    public TransactionsManager(AnswerMethodFactory answerMethodFactory,
                               KeyboardFactory keyboardFactory,
                               UserRepository userRepository,
                               TransactionService transactionService) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }


    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        User user = (User) userRepository.findByChatId(chatId);
        user.setAction(Action.SENDING_TRANSACTION);
        userRepository.save(user);
        return answerMethodFactory.getSendMessage(chatId,
                """
                         ✍️ Введите новую операцию
                        Укажите сумму и категорию через пробел. \s
                        Примеры:
                             • 1000 Авто \s
                             • 250.50 Еда \s
                             • 50 000 Квартира\s
                        """,
                null);
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        switch (callbackQuery.getData()) {
            case (CallBackData.TRANSACTIONS) -> {
                return askTransaction(callbackQuery);
            }
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return setTransaction(message);

    }

    private BotApiMethod<?> askTransaction(CallbackQuery callbackQuery) {
        User user = (User) userRepository.findByChatId(callbackQuery.getMessage().getChatId());
        user.setAction(Action.SENDING_TRANSACTION);
        userRepository.save(user);
        return answerMethodFactory.getEditMessageText(callbackQuery,
                """
                        ✍️ Введите новую операцию
                        Укажите сумму и категорию через пробел. \s
                        Примеры:
                             • 1000 Авто \s
                             • 250.50 Еда \s
                             • 50 000 Квартира\s
                        """,
                null);
    }

    private BotApiMethod<?> setTransaction(Message message) {
        User user = (User) userRepository.findByChatId(message.getChatId());
        if (transactionService.createTransaction(user, message.getText())) {
            return answerMethodFactory.getSendMessage(message.getChatId(),
                    """
                            Операция успешно сохранена""",
                    keyboardFactory.generateInlineKeyboard(
                            List.of("Назад"),
                            List.of(1),
                            List.of(BACK_ACCOUNT)
                    ));
        } else {
            return answerMethodFactory.getSendMessage(message.getChatId(),
                    """
                            ⚠️ Некорректная форма записи. \s
                            Пожалуйста, укажите сумму и категорию через пробел. \s
                            Примеры: \s
                                • 1000 Еда \s
                                • 250.50 Транспорт \s
                                • 50 000 Квартира \s
                            """,
                    null);
        }

    }
}

