package me.maksuslik.coordinator.service.bot;

import lombok.SneakyThrows;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.data.Confirmation;
import me.maksuslik.coordinator.handler.update.impl.ButtonHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;

@Service
public class BotServiceImpl implements BotService {

    @Override
    @SneakyThrows
    public void send(Confirmation confirmation, Bot bot, ButtonHandler buttonHandler) {
        var chatId = confirmation.getChatId();

        var sendMessage = new SendMessage(chatId.toString(), confirmation.getMessage());

        var rows = new ArrayList<InlineKeyboardRow>();
        rows.add(new InlineKeyboardRow(confirmation.getButtons()));
        var markupInline = new InlineKeyboardMarkup(rows);

        sendMessage.setReplyMarkup(markupInline);

        bot.getClient().execute(sendMessage);

        buttonHandler.addCallback(chatId, "yes", confirmation.getOnAccept());
        buttonHandler.addCallback(chatId, "no", confirmation.getOnDeny());
    }
}
