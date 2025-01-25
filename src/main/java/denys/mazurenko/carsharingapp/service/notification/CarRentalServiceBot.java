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
    /* Об єкт sender використовується для надсилання повідомлення через бот
    db - це контекст бази данних телеграма, для того що б ми могли збеерігати стан користувача.
    навіть якщо ми зупиняємо додаток і перезапускаємо стан зберігається,
    хотів це поєднати з якоюсь noSql базою
    але ще не розібрався з цим.

    Також реалізував простеньку аутентифікацію для
    приховування інформації про оплати/оренди від стороніх людей
    В базі зберігається один пароль для всіх юзерів */

    /* тут описані команди бота, якщо користувач вводить команду /start
    або /manager
    то в класі messageHandler буде викликатись метод start для /start
    а для /manager метод manager */
    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> messageHandler.start(ctx.chatId()))
                .build();
    }

    public Ability manager() {
        return Ability
                .builder()
                .name("manager")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> messageHandler.manager(ctx.chatId(), ctx.update().getMessage()))
                .build();
    }
    /* Тут інтерфйс визначатиме, які дії виконувати,
    якщо бот буде отримувати текстові повідомлення від користувача.
     Він буде викликати метод класу MessageHandler
     replyToStart і потім виходячи з того, що буде вводити користувач
     буде будуватись ланцюжок станів */

    public Reply replyToStart() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) ->
                messageHandler.replyToStart(upd.getMessage().getChatId(), upd.getMessage());
        return Reply.of(action, Flag.TEXT, upd ->
                messageHandler.userIsActive(upd.getMessage().getChatId()));
    }

    public void sendMessage(String message) {
        messageHandler.getActiveChat()
                .forEach(e ->
                        messageHandler.sendMessageToAuthorizedUsers(e, message)
                );
    }

    @Override
    public long creatorId() {
        return 1L;
    }
}
