package me.maksuslik.coordinator.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class ButtonHandler implements IHandler {
    private Map<Long, Map<String, Consumer<Update>>> callback = new HashMap<>();

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        if(!callback.containsKey(chatId))
            return;

        Map<String, Consumer<Update>> value = callback.get(chatId);
        String callbackData = update.getCallbackQuery().getData();
        if(!value.containsKey(callbackData))
            return;

        value.get(callbackData).accept(update);
        callback.remove(chatId);
    }

    public void addCallback(Long chatId, String queryData, Consumer<Update> onAccept) {
        if(callback.containsKey(chatId)) {
            HashMap<String, Consumer<Update>> value = new HashMap<>(callback.get(chatId));
            value.put(queryData, onAccept);
            callback.put(chatId, value);
            return;
        }

        callback.put(chatId, Map.of(queryData, onAccept));
    }
}
