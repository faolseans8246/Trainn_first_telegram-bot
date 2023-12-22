package org.example.train_third_bot.configuration;


import lombok.extern.slf4j.Slf4j;
import org.example.train_third_bot.botService.TelegramBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Slf4j
@Component
public class BotInitializer {

    @Autowired
    TelegramBotService telegramBotService;


    @EventListener({ContextRefreshedEvent.class})
    public void inits() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {

            telegramBotsApi.registerBot(telegramBotService);

        } catch (TelegramApiException t) {
            log.error("Xatolik sodir bo'ldi! -> " + t.getMessage());
        }
    }

}
