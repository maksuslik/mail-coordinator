package me.maksuslik.coordinator.bot;

import me.maksuslik.coordinator.handler.update.impl.ButtonHandler;
import me.maksuslik.coordinator.handler.update.impl.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

//@Slf4j
@Component
public class BotRequestProcessor implements BeanPostProcessor {
    @Autowired
    @Lazy
    private MessageHandler messageHandler;

    @Autowired
    private ButtonHandler buttonHandler;

    public void handleUpdate(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                this.messageHandler.handle(update);
            }

            if (update.hasCallbackQuery()) {
                this.buttonHandler.handle(update);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
