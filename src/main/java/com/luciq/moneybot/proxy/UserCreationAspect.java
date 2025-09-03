package com.luciq.moneybot.proxy;

import com.luciq.moneybot.repository.UserRepository;
import com.luciq.moneybot.service.data.Action;
import com.luciq.moneybot.service.data.Role;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
@Aspect
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationAspect {

    final UserRepository userRepository;


    @Autowired
    public UserCreationAspect(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Pointcut("execution(* com.luciq.moneybot.service.manager.start.StartManager.answerCommand(..))")
    public void answerCommandPointcut() {
    }

    @Pointcut("execution(* com.luciq.moneybot.service.manager.start.StartManager.answerCallbackQuery(..))")
    public void answerCallbackQueryPointcut() {
    }


    @Around("answerCommandPointcut() || answerCallbackQueryPointcut()")
    public Object distributeMethodAdvice(ProceedingJoinPoint joinPoint)
            throws Throwable {
        Object[] args = joinPoint.getArgs();
        User tgUser = null;
        for (Object arg : args) {
            if (arg instanceof Message message) {
                tgUser = message.getFrom();
            } else if (arg instanceof CallbackQuery callbackQuery) {
                tgUser = callbackQuery.getFrom();
            } else {
                joinPoint.proceed();
            }
        }
        if (!userRepository.existsByChatId(tgUser.getId())) {
            com.luciq.moneybot.entity.User user =
                    com.luciq.moneybot.entity.User.builder()
                            .chatId(tgUser.getId())
                            .role(Role.USER)
                            .action(Action.FREE)
                            .build();
            userRepository.save(user);
        }
        return joinPoint.proceed();
    }

}
