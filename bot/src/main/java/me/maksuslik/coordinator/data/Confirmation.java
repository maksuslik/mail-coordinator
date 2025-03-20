package me.maksuslik.coordinator.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@Getter
public class Confirmation {

    Long chatId;
    String message;
    Consumer<Update> onAccept;
    Consumer<Update> onDeny;

    public List<InlineKeyboardButton> getButtons() {
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("✅ Подтвердить");
        inlineKeyboardButton1.setCallbackData("yes");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("❌ Отклонить");
        inlineKeyboardButton2.setCallbackData("no");

        buttonsRow.add(inlineKeyboardButton1);
        buttonsRow.add(inlineKeyboardButton2);
        return buttonsRow;
    }
}
