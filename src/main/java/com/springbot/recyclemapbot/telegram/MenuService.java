package com.springbot.recyclemapbot.telegram;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {


    //Main menu
    public SendMessage getMainMenuKeyboard(Long chatId) {
        SendMessage m = new SendMessage();
        m.setText("В моем арсенале много точек, отправь геолокацию места, где будем искать точки.\n\nДля этого нажми на скрепку -> значок геолокации -> отправить эту локацию");
        m.setChatId(chatId);
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        row1.add(new KeyboardButton("/createSubscribe"));
        row1.add(new KeyboardButton("/getSubscribes"));
        row2.add((new KeyboardButton("/application")));
        row2.add((new KeyboardButton("/getApplications")));
        row3.add((new KeyboardButton("/help")));
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        replyKeyboardMarkup.setKeyboard(keyboard);
        m.setReplyMarkup(replyKeyboardMarkup);
        return m;
    }


}