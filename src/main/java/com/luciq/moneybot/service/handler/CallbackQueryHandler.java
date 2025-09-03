package com.luciq.moneybot.service.handler;

import com.luciq.moneybot.service.NavigationService;
import com.luciq.moneybot.service.factory.AnswerMethodFactory;
import com.luciq.moneybot.service.manager.account.AccountManager;
import com.luciq.moneybot.service.manager.category.CategoryManager;
import com.luciq.moneybot.service.manager.feedback.FeedbackManager;
import com.luciq.moneybot.service.manager.help.HelpManager;
import com.luciq.moneybot.service.manager.menu.MenuManager;
import com.luciq.moneybot.service.manager.settings.SettingsManager;
import com.luciq.moneybot.service.manager.start.StartManager;
import com.luciq.moneybot.service.manager.transaction.TransactionsManager;
import com.luciq.moneybot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static com.luciq.moneybot.service.data.CallBackData.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallbackQueryHandler {


    final FeedbackManager feedbackManager;

    final StartManager startManager;

    final CategoryManager categoryManager;

    final HelpManager helpManager;

    final MenuManager menuManager;

    final AccountManager accountManager;

    final TransactionsManager transactionsManager;

    final NavigationService navigationService;
    final SettingsManager settingsManager;
    final AnswerMethodFactory answerMethodFactory;


    @Autowired
    public CallbackQueryHandler(FeedbackManager feedbackManager,
                                HelpManager helpManager,
                                MenuManager menuManager,
                                AccountManager accountManager,
                                TransactionsManager transactionsManager,
                                NavigationService navigationService,
                                StartManager startManager,
                                CategoryManager categoryManager,
                                SettingsManager settingsManager,
                                AnswerMethodFactory answerMethodFactory) {
        this.feedbackManager = feedbackManager;
        this.helpManager = helpManager;
        this.menuManager = menuManager;
        this.accountManager = accountManager;
        this.transactionsManager = transactionsManager;
        this.navigationService = navigationService;
        this.startManager = startManager;
        this.categoryManager = categoryManager;
        this.settingsManager = settingsManager;
        this.answerMethodFactory = answerMethodFactory;
    }

    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        switch (callbackData) {
            case FEEDBACK -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), FEEDBACK.toUpperCase());
                return feedbackManager.answerCallbackQuery(callbackQuery, bot);
            }
            case HELP -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), HELP.toUpperCase());
                return helpManager.answerCallbackQuery(callbackQuery, bot);
            }
            case MENU -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), MENU.toUpperCase());
                return menuManager.answerCallbackQuery(callbackQuery, bot);
            }
            case ACCOUNT, BACK_ACCOUNT -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), ACCOUNT.toUpperCase());
                return accountManager.answerCallbackQuery(callbackQuery, bot);
            }
            case TRANSACTIONS -> {
                return transactionsManager.answerCallbackQuery(callbackQuery, bot);
            }
            case CATEGORY -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), CATEGORY.toUpperCase());
                return categoryManager.answerCallbackQuery(callbackQuery, bot);
            }
            case SETTINGS, SET_LIMIT, DELETE_HISTORY, BACK_SETTINGS -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), SETTINGS.toUpperCase());
                return settingsManager.answerCallbackQuery(callbackQuery, bot);
            }
            case BACK -> {
                return goBack(callbackQuery, bot);
            }
        }
       return null;
    }

    private BotApiMethod<?> goBack(CallbackQuery callbackQuery, Bot bot) {
        List<String> userNavigations = navigationService.getUserNavigation(callbackQuery.getMessage().getChatId());
        switch (userNavigations.get(1)) {
            case "START" -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), "START");
                return startManager.answerCallbackQuery(callbackQuery, bot);
            }
            case "FEEDBACK" -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), "FEEDBACK");
                return feedbackManager.answerCallbackQuery(callbackQuery, bot);
            }
            case "HELP" -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), "HELP");
                return helpManager.answerCallbackQuery(callbackQuery, bot);
            }
            case "MENU" -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), "MENU");
                return menuManager.answerCallbackQuery(callbackQuery, bot);
            }
            case "ACCOUNT" -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), "ACCOUNT");
                return accountManager.answerCallbackQuery(callbackQuery, bot);
            }
            case "TRANSACTIONS" -> {
                return transactionsManager.answerCallbackQuery(callbackQuery, bot);
            }
            case "CATEGORY" -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), "CATEGORY");
                return categoryManager.answerCallbackQuery(callbackQuery, bot);
            }
            case "SETTINGS" -> {
                navigationService.saveOrUpdateUserNavigation(callbackQuery.getMessage().getChatId(), "SETTINGS");
                return settingsManager.answerCallbackQuery(callbackQuery, bot);
            }

        }
        return null;
    }
}
