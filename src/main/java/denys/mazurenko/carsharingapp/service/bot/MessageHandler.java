package denys.mazurenko.carsharingapp.service.bot;

import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class MessageHandler {
    private final SilentSender sender;

    public MessageHandler(SilentSender sender) {
        this.sender = sender;
    }

    public void start(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(TelegramBotMessages.getGREETING_MESSAGE());
        sender.execute(message);
    }

    public void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sender.execute(sendMessage);
    }
}
