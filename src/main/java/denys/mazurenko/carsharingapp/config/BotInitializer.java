package denys.mazurenko.carsharingapp.config;

import denys.mazurenko.carsharingapp.exception.TelegramBotInitializationException;
import denys.mazurenko.carsharingapp.service.notification.bot.RentalServiceBot;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class BotInitializer {
    private final RentalServiceBot rentalServiceBot;

    @EventListener({ContextRefreshedEvent.class})
    public void onContextRefreshed() {
        try {
            TelegramBotsApi bot = new TelegramBotsApi(DefaultBotSession.class);
            bot.registerBot(rentalServiceBot);
        } catch (TelegramApiException e) {
            throw new TelegramBotInitializationException(
                    String.format("Can't initialize bot "
                            + rentalServiceBot.getClass().getSimpleName()
                            + " %s", e)
            );
        }
    }
}
