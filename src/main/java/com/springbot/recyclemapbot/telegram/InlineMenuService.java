package com.springbot.recyclemapbot.telegram;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
public class InlineMenuService {

    public SendMessage getInlineMessageButtonsForDistance(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите радиус поиска в минутах или в метрах\n\n");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("5 минут");
        inlineKeyboardButton.setCallbackData("5_минут");
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("15 минут");
        inlineKeyboardButton1.setCallbackData("15_минут");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("30 минут");
        inlineKeyboardButton2.setCallbackData("30_минут");
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("1 км");
        inlineKeyboardButton3.setCallbackData("1_км");
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText("5 км");
        inlineKeyboardButton4.setCallbackData("5_км");
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        inlineKeyboardButton5.setText("10 км");
        inlineKeyboardButton5.setCallbackData("10_км");
        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();
        inlineKeyboardButton6.setText("ближайшие точки");
        inlineKeyboardButton6.setCallbackData("closest");
        rowInline.add(inlineKeyboardButton);
        rowInline.add(inlineKeyboardButton1);
        rowInline.add(inlineKeyboardButton2);
        rowInline2.add(inlineKeyboardButton3);
        rowInline2.add(inlineKeyboardButton4);
        rowInline2.add(inlineKeyboardButton5);
        rowInline3.add(inlineKeyboardButton6);
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);

        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        return message;
    }

    public InlineKeyboardMarkup getInlineMessageButtonsForSubscribe(Long chatId, Long subscribeId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Отписаться");
        inlineKeyboardButton.setCallbackData("Айди подписки " + subscribeId);
        rowInline.add(inlineKeyboardButton);
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public SendMessage getInlineMessageButtonsForFractions(Long chatId, BotState botState) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline5 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("\uD83D\uDDDE Бумага");
        inlineKeyboardButton.setCallbackData("BUMAGA");
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("\uD83E\uDD64 Пластик");
        inlineKeyboardButton1.setCallbackData("PLASTIK");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("\uD83E\uDED9 Стекло");
        inlineKeyboardButton2.setCallbackData("STEKLO");
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("⛓ Металл");
        inlineKeyboardButton3.setCallbackData("METALL");
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText("\uD83E\uDDC3 Тетра пак");
        inlineKeyboardButton4.setCallbackData("TETRA_PAK");
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        inlineKeyboardButton5.setText("\uD83D\uDC55 Одежда");
        inlineKeyboardButton5.setCallbackData("ODEZHDA");
        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();
        inlineKeyboardButton6.setText("\uD83D\uDCA1 Лампочки");
        inlineKeyboardButton6.setCallbackData("LAMPOCHKI");
        InlineKeyboardButton inlineKeyboardButton7 = new InlineKeyboardButton();
        inlineKeyboardButton7.setText("\uD83D\uDD73 Крышечки");
        inlineKeyboardButton7.setCallbackData("KRYSHECHKI");
        InlineKeyboardButton inlineKeyboardButton8 = new InlineKeyboardButton();
        inlineKeyboardButton8.setText("\uD83C\uDF9B Бытовая техника");
        inlineKeyboardButton8.setCallbackData("BYTOVAJA_TEHNIKA");
        InlineKeyboardButton inlineKeyboardButton9 = new InlineKeyboardButton();
        inlineKeyboardButton9.setText("\uD83D\uDD0B Батарейки");
        inlineKeyboardButton9.setCallbackData("BATAREJKI");
        InlineKeyboardButton inlineKeyboardButton10 = new InlineKeyboardButton();
        inlineKeyboardButton10.setText("⚙️ Шины");
        inlineKeyboardButton10.setCallbackData("SHINY");
        InlineKeyboardButton inlineKeyboardButton11 = new InlineKeyboardButton();
        inlineKeyboardButton11.setText("⚠️ Опасные отходы");
        inlineKeyboardButton11.setCallbackData("OPASNYE_OTHODY");
        InlineKeyboardButton inlineKeyboardButton12 = new InlineKeyboardButton();
        inlineKeyboardButton12.setText("\uD83E\uDD14 Иное");
        inlineKeyboardButton12.setCallbackData("INOE");
        rowInline.add(inlineKeyboardButton);
        rowInline.add(inlineKeyboardButton1);
        rowInline.add(inlineKeyboardButton2);
        rowInline2.add(inlineKeyboardButton3);
        rowInline2.add(inlineKeyboardButton4);
        rowInline2.add(inlineKeyboardButton5);
        rowInline3.add(inlineKeyboardButton6);
        rowInline3.add(inlineKeyboardButton7);
        rowInline3.add(inlineKeyboardButton8);
        rowInline4.add(inlineKeyboardButton9);
        rowInline4.add(inlineKeyboardButton10);
        rowInline5.add(inlineKeyboardButton11);
        rowInline5.add(inlineKeyboardButton12);
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline4);
        rowsInline.add(rowInline5);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        switch (botState.toString()){
            case  "SUBSCRIBE":
                message.setText("Выберите необходимые фракции\nПосле того, как закончите выбор, нажмите /search ");
                break;
            case "APPLICATION":
                message.setText("Нажмите /setTitle чтобы выбрать название вашей заявки");
                break;
            default:
        }
        return message;
    }
}
