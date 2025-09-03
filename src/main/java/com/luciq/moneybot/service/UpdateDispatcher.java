package com.luciq.moneybot.service;

import com.luciq.moneybot.service.handler.CallbackQueryHandler;
import com.luciq.moneybot.service.handler.CommandHandler;
import com.luciq.moneybot.service.handler.MessageHandler;
import com.luciq.moneybot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UpdateDispatcher {

    final CallbackQueryHandler callbackQueryHandler;
    final MessageHandler messageHandler;
    final CommandHandler commandHandler;

    @Autowired
    public UpdateDispatcher(CallbackQueryHandler callbackQueryHandler,
                            MessageHandler messageHandler,
                            CommandHandler commandHandler) {
        this.callbackQueryHandler = callbackQueryHandler;
        this.messageHandler = messageHandler;
        this.commandHandler = commandHandler;
    }

    public BotApiMethod<?> distribute(Update update, Bot bot) {
        if (update.hasCallbackQuery()) {
            return callbackQueryHandler.answer(update.getCallbackQuery(), bot);
        }
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                if (message.getText().charAt(0) == '/') {
                    return commandHandler.answer(message, bot);
                }
            }
            return messageHandler.answer(message, bot);
        }
        log.info("Unsupported update");
        return null;
    }
}
