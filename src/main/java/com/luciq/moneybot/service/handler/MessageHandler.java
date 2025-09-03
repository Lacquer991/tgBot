package com.luciq.moneybot.service.handler;

import com.luciq.moneybot.repository.UserRepository;
import com.luciq.moneybot.entity.User;
import com.luciq.moneybot.service.manager.settings.SettingsManager;
import com.luciq.moneybot.service.manager.transaction.TransactionsManager;
import com.luciq.moneybot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageHandler {

    final UserRepository userRepository;

    final TransactionsManager transactionsManager;
    private final SettingsManager settingsManager;

    @Autowired
    public MessageHandler(UserRepository userRepository,
                          TransactionsManager transactionsManager,
                          SettingsManager settingsManager) {
        this.userRepository = userRepository;
        this.transactionsManager = transactionsManager;
        this.settingsManager = settingsManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        User user = (User) userRepository.findByChatId(message.getChatId());
        switch (user.getAction()) {
            case SENDING_TRANSACTION -> {
                return transactionsManager.answerMessage(message, bot);
            }
            case SENDING_LIMIT -> {
                return settingsManager.answerMessage(message, bot);
            }
        }
        return null;
    }
}
