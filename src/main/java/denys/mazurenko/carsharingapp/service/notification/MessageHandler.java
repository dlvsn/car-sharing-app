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
        message.setText(TelegramBotMessages.getGREETING_MESSAGE());
        sender.execute(message);
        chatState.put(chatId, UserState.START);
    }

    public void replyToStart(Long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/stop")) {
            stopChat(chatId);
        }
        switch (chatState.get(chatId)) {
            case START -> replyToPassword(chatId, message);
            case AWAITING_SECURE_CODE -> verifyPassword(message, chatId);
            default -> sendDefaultMessage(chatId);
        }
    }

    public void sendMessage(Long chatId, String message) {
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

    private void sendDefaultMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Your chat state is " + chatState.get(chatId).name());
        sender.execute(sendMessage);
    }

    private void replyToPassword(Long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(TelegramBotMessages.enterPassword(message.getFrom().getFirstName()));
        sender.execute(sendMessage);
        chatState.put(chatId, UserState.AWAITING_SECURE_CODE);
    }

    private void verifyPassword(Message message, Long chatId) {
        if (securityService.isPasswordMatches(message.getText())) {
            sendMessageAfterCheckingPassword(chatId, TelegramBotMessages.getPASSWORD_CORRECT());
            chatState.put(chatId, UserState.AUTHORIZED);
        } else {
            sendMessageAfterCheckingPassword(chatId, TelegramBotMessages.getPASSWORD_INCORRECT());
        }
    }

    private void sendMessageAfterCheckingPassword(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sender.execute(sendMessage);
    }

    private void stopChat(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(TelegramBotMessages.getGOODBYE_MESSAGE());
        sender.execute(sendMessage);
        chatState.remove(chatId);
    }
}
