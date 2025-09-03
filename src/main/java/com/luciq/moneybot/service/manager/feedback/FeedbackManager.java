package com.luciq.moneybot.service.manager.feedback;

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
public class FeedbackManager extends AbstractManager {

    final AnswerMethodFactory answerMethodFactory;

    final KeyboardFactory keyboardFactory;

    static final String FEEDBACK_TEXT = """
            📍 Ссылки для обратной связи
            • Telegram -
            """;

    @Autowired
    public FeedbackManager(AnswerMethodFactory answerMethodFactory,
                           KeyboardFactory keyboardFactory) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
    }


    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return answerMethodFactory.getSendMessage(message.getChatId(),
                FEEDBACK_TEXT,
                keyboardFactory.generateInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(BACK)));

    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return answerMethodFactory.getEditMessageText(callbackQuery,
                FEEDBACK_TEXT,
                keyboardFactory.generateInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(BACK)
                ));
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }
}
