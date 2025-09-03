package com.luciq.moneybot.service.manager.category;

import static com.luciq.moneybot.service.data.CallBackData.*;

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
public class CategoryManager extends AbstractManager {

    final AnswerMethodFactory answerMethodFactory;

    final KeyboardFactory keyboardFactory;

    final CategoryService categoryService;


    @Autowired
    public CategoryManager(AnswerMethodFactory answerMethodFactory,
                           KeyboardFactory keyboardFactory,
                           CategoryService categoryService) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.categoryService = categoryService;
    }


    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        String categoryText =  categoryService.getAllCategories(chatId);
        return answerMethodFactory.getSendMessage(chatId,
               categoryText,
                keyboardFactory.generateInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(BACK)
                ));
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String categoryText =  categoryService.getAllCategories(chatId);
        return answerMethodFactory.getEditMessageText(callbackQuery,
                categoryText,
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
