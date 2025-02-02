package denys.mazurenko.carsharingapp.service.notification.bot;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import denys.mazurenko.carsharingapp.security.telegram.TelegramSecurityService;
import denys.mazurenko.carsharingapp.service.notification.util.MessageBuilder;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class RentalServiceBot extends AbilityBot {
    private final TelegramSecurityService securityService;
    private SilentSender sender;
    private Map<Long, UserState> chatState;

    protected RentalServiceBot(
            @Value("${telegram.bot.token}")
            String botToken,
            @Value("${telegram.bot.name}")
            String botUsername,
            TelegramSecurityService securityService) {
        super(botToken, botUsername);
        this.securityService = securityService;
    }

    @PostConstruct
    public void init() {
        this.sender = this.silent;
        this.chatState = this.db.getMap("chatState");
    }

    @Override
    public long creatorId() {
        return 1L;
    }

    public void sendNotification(String message) {
        getActiveChat()
                .forEach(e ->
                        sendMessageToAuthorizedUsers(e, message)
                );
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> startMessage(ctx.chatId()))
                .build();
    }

    public Ability manager() {
        return Ability
                .builder()
                .name("manager")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> handleManager(ctx.chatId(), ctx.update().getMessage()))
                .build();
    }

    public Reply handleUserCommands() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) ->
                handleCommands(upd.getMessage().getChatId(), upd.getMessage());
        return Reply.of(action, Flag.TEXT, upd ->
                userIsActive(upd.getMessage().getChatId()));
    }

    private void handleCommands(Long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/stop")) {
            stopChat(chatId);
        }
        switch (chatState.get(chatId)) {
            case START -> handleManager(chatId, message);
            case AWAITING_SECURE_CODE -> verifyPassword(message, chatId);
            default -> sendOnUnknownCommand(chatId, message);
        }
    }

    private void startMessage(Long chatId) {
        sendMessage(chatId, MessageBuilder.TelegramBotMessageTemplates
                .START_MESSAGE.getText());
        chatState.put(chatId, UserState.START);
    }

    private void handleManager(Long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/manager")) {
            sendMessage(chatId, String.format(
                    MessageBuilder.TelegramBotMessageTemplates.ENTER_PASSWORD.getText(),
                    message.getFrom().getFirstName()));
            chatState.put(chatId, UserState.AWAITING_SECURE_CODE);
        } else {
            sendMessage(chatId, String.format(
                    MessageBuilder.TelegramBotMessageTemplates.UNKNOWN_COMMAND.getText(),
                    message.getText()));
        }
    }

    private void sendOnUnknownCommand(Long chatId, Message message) {
        sendMessage(chatId, String.format(
                MessageBuilder.TelegramBotMessageTemplates.UNKNOWN_COMMAND.getText(),
                message.getText()));
    }

    private void sendMessageToAuthorizedUsers(Long chatId, String message) {
        if (chatState.get(chatId) == UserState.AUTHORIZED) {
            sendMessage(chatId, message);
        }
    }

    private Set<Long> getActiveChat() {
        return chatState.keySet();
    }

    private boolean userIsActive(Long chatId) {
        return chatState.containsKey(chatId);
    }

    private void verifyPassword(Message message, Long chatId) {
        if (securityService.isPasswordMatches(message.getText())) {
            sendMessageAfterCheckingPassword(chatId, true);
            chatState.put(chatId, UserState.AUTHORIZED);
        } else {
            sendMessageAfterCheckingPassword(chatId, false);
        }
    }

    private void sendMessageAfterCheckingPassword(Long chatId, boolean isPasswordValid) {
        String text = isPasswordValid
                ? MessageBuilder.TelegramBotMessageTemplates.PASSWORD_CORRECT.getText()
                : MessageBuilder.TelegramBotMessageTemplates.PASSWORD_INCORRECT.getText();
        sendMessage(chatId, text);
    }

    private void stopChat(Long chatId) {
        sendMessage(chatId, MessageBuilder.TelegramBotMessageTemplates
                .GOODBYE_MESSAGE.getText()
        );
        chatState.remove(chatId);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sender.execute(sendMessage);
    }
}
