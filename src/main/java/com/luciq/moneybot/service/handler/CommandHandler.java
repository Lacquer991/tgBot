package com.luciq.moneybot.service.handler;

import com.luciq.moneybot.entity.User;
import com.luciq.moneybot.repository.UserRepository;
import com.luciq.moneybot.service.NavigationService;
import com.luciq.moneybot.service.data.Action;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;


import static com.luciq.moneybot.service.data.Command.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommandHandler {

    final StartManager startManager;

    final FeedbackManager feedbackManager;

    final HelpManager helpManager;

    final MenuManager menuManager;

    final AccountManager accountManager;

    final TransactionsManager transactionsManager;

    final CategoryManager categoryManager;

    final NavigationService navigationService;

    final UserRepository userRepository;
    private final SettingsManager settingsManager;

    @Autowired
    public CommandHandler(FeedbackManager feedbackManager,
                          HelpManager helpManager,
                          StartManager startManager,
                          MenuManager menuManager,
                          AccountManager accountManager,
                          TransactionsManager transactionsManager,
                          CategoryManager categoryManager,
                          NavigationService navigationService,
                          UserRepository userRepository,
                          SettingsManager settingsManager) {
        this.feedbackManager = feedbackManager;
        this.helpManager = helpManager;
        this.startManager = startManager;
        this.menuManager = menuManager;
        this.accountManager = accountManager;
        this.transactionsManager = transactionsManager;
        this.categoryManager = categoryManager;
        this.navigationService = navigationService;
        this.userRepository = userRepository;
        this.settingsManager = settingsManager;
    }


    public BotApiMethod<?> answer(Message message, Bot bot) {
        User user = (User) userRepository.findByChatId(message.getChatId());
        if (user != null) {
            user.setAction(Action.FREE);
            userRepository.save(user);
        }
        String command = message.getText();
        switch (command) {
            case START_CMD -> {
                navigationService.saveOrUpdateUserNavigation(message.getChatId(), "START");
                return startManager.answerCommand(message, bot);
            }
            case FEEDBACK_CMD -> {
                navigationService.saveOrUpdateUserNavigation(message.getChatId(), "FEEDBACK");
                return feedbackManager.answerCommand(message, bot);
            }
            case HELP_CMD -> {
                navigationService.saveOrUpdateUserNavigation(message.getChatId(), "HELP");
                return helpManager.answerCommand(message, bot);
            }
            case MENU_CMD -> {
                navigationService.saveOrUpdateUserNavigation(message.getChatId(), "MENU");
                return menuManager.answerCommand(message, bot);
            }
            case ACCOUNT_CMD -> {
                navigationService.saveOrUpdateUserNavigation(message.getChatId(), "ACCOUNT");
                return accountManager.answerCommand(message, bot);
            }
            case TRANSACTIONS_CMD -> {
                return transactionsManager.answerCommand(message, bot);
            }
            case CATEGORIES_CMD -> {
                navigationService.saveOrUpdateUserNavigation(message.getChatId(), "CATEGORY");
                return categoryManager.answerCommand(message, bot);
            }
            case SETTINGS_CMD -> {
                navigationService.saveOrUpdateUserNavigation(message.getChatId(), "SETTINGS");
                return settingsManager.answerCommand(message, bot);
            }
            default -> {
                return defaultAnswer(message);
            }
        }
    }

    private BotApiMethod<?> defaultAnswer(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("Неподдерживаемая команда")
                .build();
    }

}
