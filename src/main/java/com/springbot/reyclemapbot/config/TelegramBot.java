package com.springbot.reyclemapbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbot.reyclemapbot.DTO.PointDTO;
import com.springbot.reyclemapbot.DTO.SubscribeDTO;
import com.springbot.reyclemapbot.model.Subscribe;
import com.springbot.reyclemapbot.model.User;
import com.springbot.reyclemapbot.repository.SubscribeRepository;
import com.springbot.reyclemapbot.repository.UserRepository;
import com.springbot.reyclemapbot.rest.PointController;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@EnableAsync
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;

    private final SubscribeRepository subscribeRepository;

    private final PointController pointController;

    private final BotConfig config;

    @Override
    public String getBotUsername() { return config.getBotUsername(); }

    @Override
    public String getBotToken() { return config.getToken(); }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            Message message = update.getMessage();
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String memberName = update.getMessage().getFrom().getFirstName();

            switch (messageText){
                case "/start":
                    registerUser(update.getMessage());
                    getLocation(chatId);
                    break;
                case "/subscribe":
                   // saveSubcribe()
               /* case "/location":
                    getLocation(chatId);
                    break;*/
                default: log.info("Unexpected message");
            }
        }  else if (update.getMessage().hasLocation()) {
           /* String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();*/
           /* if (callbackData.equals("location")) {*/
            ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove(true);
            Location location = update.getMessage().getLocation();
            Double lon = location.getLongitude();
            Double lat = location.getLatitude();
            try {
                List<PointDTO> points =  pointController.getRecommendation(lon, lat, 1.00);
                for(PointDTO pointDTO: points){
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    message.setText("Точка: " + pointDTO.getUrl() + "\nНазвание:" + pointDTO.getTitle()
                            + "\nАдрес:" + pointDTO.getAddress());
                    try {
                        execute(message);
                        log.info("Reply sent");
                    } catch (TelegramApiException e){
                        log.error(e.getMessage());
                    }
                }
                SendMessage messageAfter = new SendMessage();
                messageAfter.setChatId(update.getMessage().getChatId());
                messageAfter.setText("/subscribe");
                execute(messageAfter);
                log.info("Reply sent");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            // subscribeRepository.saveSubscribe(update.getMessage().getChatId(), lon, lat);
            log.info("Location" + location);
        }
    }

    private void saveSubscribe(Long chatId, Location location){
        Double lon = location.getLongitude();
        Double lat = location.getLatitude();

    }

   /* private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Привет, " + userName + "!\nRecyclemap bot на связи, чтобы помочь тебе найти ближайшие пункты приема отходов и быть в курсе всеx изменений.\n" +
                "Тык /start и полетели!");

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }
*/
    private void start(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("В моем арсенале много пунктов, просто отправь мне свою геопозицию, чтобы я  подобрал ближайшие для тебя локации");
        try {
            execute(message);
            getLocation(chatId);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void getLocation(long chatId){
        //Location location = message.getLocation();
        SendMessage m = new SendMessage();
        m.setText("В моем арсенале много пунктов, просто отправь мне свою геопозицию, чтобы я  подобрал ближайшие для тебя локации");
        m.setChatId(chatId);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton("location");
        keyboardButton.setRequestLocation(true);
        keyboardRow.add(keyboardButton);
        keyboard.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        m.setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(m);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }


    private void registerUser(Message msg) {

        if(userRepository.findById(msg.getChatId()).isEmpty()){

            Long chatId = msg.getChatId();
            Chat chat = msg.getChat();
            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUsername(chat.getUserName());

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }

    private final String ECOADVICES_URL = "https://recyclemap-api-master.rc.geosemantica.ru/public/ecoadvices/random";


    @Scheduled(fixedDelay = 120000)
    public void sendEcoAdvice() throws IOException {
        URL url = new URL(ECOADVICES_URL);
        ObjectMapper mapper = new ObjectMapper();
        String advice = mapper.readTree(url).get("data").get("advice").asText();
        List<Long> chatIds = this.userRepository.getAllChatIds();
        for(Long chatId : chatIds){
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(advice);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

}
