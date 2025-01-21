package denys.mazurenko.carsharingapp.service.notification;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import denys.mazurenko.carsharingapp.security.telegram.TelegramSecurityService;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CarRentalServiceBot extends AbilityBot {
    private final MessageHandler messageHandler;

    protected CarRentalServiceBot(
            @Value("${telegram.bot.token}")
            String botToken,
            @Value("${telegram.bot.name}")
            String botUsername,
            @Autowired
            TelegramSecurityService securityService) {
        super(botToken, botUsername);
        messageHandler = new MessageHandler(silent, db, securityService);
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

    public Reply replyToStart() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) ->
                messageHandler.replyToStart(upd.getMessage().getChatId(), upd.getMessage());
        return Reply.of(action, Flag.TEXT, upd ->
                messageHandler.userIsActive(upd.getMessage().getChatId()));
    }

    public void sendMessage(String message) {
        messageHandler.getActiveChat().forEach(e -> messageHandler.sendMessage(e, message));
    }

    @Override
    public long creatorId() {
        return 1L;
    }
}
