package denys.mazurenko.carsharingapp.service.bot;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;

@Component
public class CarRentalServiceBot extends AbilityBot {
    private final MessageHandler messageHandler;

    protected CarRentalServiceBot(
            @Value("${telegram.bot.token}")
            String botToken,
            @Value("${telegram.bot.name}")
            String botUsername) {
        super(botToken, botUsername);
        messageHandler = new MessageHandler(silent);
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> messageHandler.start(ctx.chatId()))
                .build();
    }

    public void sendMessage(String message) {
        //456291507L
        messageHandler.sendMessage(323755547L, message);
    }

    @Override
    public long creatorId() {
        return 1L;
    }
}
