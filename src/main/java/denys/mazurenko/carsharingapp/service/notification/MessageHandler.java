package denys.mazurenko.carsharingapp.service.notification;

import denys.mazurenko.carsharingapp.security.telegram.TelegramSecurityService;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class MessageHandler {
    private final TelegramSecurityService securityService;
    private final Map<Long, UserState> chatState;
    private final SilentSender sender;

    public MessageHandler(SilentSender sender,
                          DBContext dbContext,
                          @Autowired
                          TelegramSecurityService securityService) {
        this.sender = sender;
        this.chatState = dbContext.getMap("chatStates");
        this.securityService = securityService;
    }

    public void start(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(MessageBuilder
                .TelegramBotMessageType
                .START_MESSAGE.getType());
        sender.execute(message);
        chatState.put(chatId, UserState.START);
    }

    public void manager(Long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (message.getText().equalsIgnoreCase("/manager")) {
            sendMessage.setText(String.format(
                    MessageBuilder.TelegramBotMessageType.ENTER_PASSWORD.getType(),
                    message.getFrom().getFirstName()));
            sender.execute(sendMessage);
            chatState.put(chatId, UserState.AWAITING_SECURE_CODE);
        } else {
            sendMessage.setText(String.format(
                    MessageBuilder.TelegramBotMessageType.UNKNOWN_COMMAND.getType(),
                    message.getText()));
            sender.execute(sendMessage);
        }
    }

    public void replyToStart(Long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/stop")) {
            stopChat(chatId);
        }
        switch (chatState.get(chatId)) {
            case START -> manager(chatId, message);
            case AWAITING_SECURE_CODE -> verifyPassword(message, chatId);
            default -> sendOnUnknownCommand(chatId, message);
        }
    }

    public void sendMessageToAuthorizedUsers(Long chatId, String message) {
        if (chatState.get(chatId) == UserState.AUTHORIZED) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            sender.execute(sendMessage);
        }
    }

    public Set<Long> getActiveChat() {
        return chatState.keySet();
    }

    public boolean userIsActive(Long chatId) {
        return chatState.containsKey(chatId);
    }

    private void sendOnUnknownCommand(Long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(String.format(
                MessageBuilder.TelegramBotMessageType.UNKNOWN_COMMAND.getType(),
                message.getText()));
        sender.execute(sendMessage);
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
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String text = isPasswordValid
                ? MessageBuilder.TelegramBotMessageType.PASSWORD_CORRECT.getType()
                : MessageBuilder.TelegramBotMessageType.PASSWORD_INCORRECT.getType();
        sendMessage.setText(text);
        sender.execute(sendMessage);
    }

    private void stopChat(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(
                MessageBuilder
                .TelegramBotMessageType
                .GOODBYE_MESSAGE.getType()
        );
        sender.execute(sendMessage);
        chatState.remove(chatId);
    }
}
