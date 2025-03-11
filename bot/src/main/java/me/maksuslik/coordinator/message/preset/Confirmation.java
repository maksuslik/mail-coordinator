package me.maksuslik.coordinator.message.preset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import me.maksuslik.coordinator.bot.Bot;
import me.maksuslik.coordinator.handler.ButtonHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Builder
@AllArgsConstructor
@Getter
public class Confirmation {
    private Long chatId;

    private String message;

    private Consumer<Update> onAccept;

    private Consumer<Update> onDeny;

    public void send(Bot bot, ButtonHandler buttonHandler) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> buttonsRow = getButtons();

        rowsInline.add(buttonsRow);

        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        bot.execute(sendMessage);

        buttonHandler.addCallback(chatId, "yes", onAccept);
        buttonHandler.addCallback(chatId, "no", onDeny);
    }

    private List<InlineKeyboardButton> getButtons() {
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("✅ Подтвердить");
        inlineKeyboardButton1.setCallbackData("yes");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("❌ Отклонить");
        inlineKeyboardButton2.setCallbackData("no");

        buttonsRow.add(inlineKeyboardButton1);
        buttonsRow.add(inlineKeyboardButton2);
        return buttonsRow;
    }
}
