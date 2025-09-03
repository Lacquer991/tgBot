package com.luciq.moneybot.service.manager.start;

import com.luciq.moneybot.service.manager.AbstractManager;
import com.luciq.moneybot.service.factory.AnswerMethodFactory;
import com.luciq.moneybot.service.factory.KeyboardFactory;
import com.luciq.moneybot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.luciq.moneybot.service.data.CallBackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartManager extends AbstractManager {


    final AnswerMethodFactory answerMethodFactory;

    final KeyboardFactory keyboardFactory;

    static final String START_TEXT = """
                        💰 Добро пожаловать в Финансовый трекер!
                        Этот бот поможет тебе:
                        	•	📌 быстро фиксировать расходы и доходы
                        	•	📊 смотреть статистику за день, неделю или месяц
                        	•	💡 следить за бюджетом и не выходить за лимиты
                        	•	📑 выгружать свои данные в CSV/Excel - В РАЗРАБОТКЕ
                        """;


    @Autowired
    public StartManager(AnswerMethodFactory answerMethodFactory,
                        KeyboardFactory keyboardFactory) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public SendMessage answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        return answerMethodFactory.getSendMessage(
                chatId,
                START_TEXT,
                keyboardFactory.generateInlineKeyboard(
                        List.of("Личный кабинет", "Доступный функционал", "Обратная связь"),
                        List.of(1,2),
                        List.of(ACCOUNT, HELP, FEEDBACK)

                ));
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                START_TEXT,
                keyboardFactory.generateInlineKeyboard(
                        List.of("Личный кабинет", "Доступный функционал", "Обратная связь"),
                        List.of(1,2),
                        List.of(ACCOUNT, HELP, FEEDBACK)

                ));
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }
}
