package denys.mazurenko.carsharingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableScheduling
public class CarSharingAppApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication
                .run(CarSharingAppApplication.class, args);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(ctx.getBean("carRentalServiceBot", AbilityBot.class));
        } catch (TelegramApiException e) {
            throw new RuntimeException(
                    String.format("Can't start telegram bot: %s", e.getMessage())
            );
        }
    }
}
