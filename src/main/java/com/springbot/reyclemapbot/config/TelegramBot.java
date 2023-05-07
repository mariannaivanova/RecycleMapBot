package com.springbot.reyclemapbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbot.reyclemapbot.DTO.PointPayload;
import com.springbot.reyclemapbot.DTO.SubscribeString;
import com.springbot.reyclemapbot.model.User;
import com.springbot.reyclemapbot.repository.SubscribeRepository;
import com.springbot.reyclemapbot.repository.UserRepository;
import com.springbot.reyclemapbot.rest.PointController;
import com.springbot.reyclemapbot.rest.SubscribeController;
import com.springbot.reyclemapbot.serviceImplementation.FractionServiceImpl;
import com.springbot.reyclemapbot.serviceImplementation.PointServiceImpl;
import jakarta.validation.constraints.NotNull;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@EnableAsync
@Setter
@Getter
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;

    private final SubscribeRepository subscribeRepository;

    private final PointController pointController;

    private final SubscribeController subscribeController;

    private final FractionServiceImpl fractionService;

    private final PointServiceImpl pointService;

    private final BotConfig config;

    private Double lon;

    private Double lat;

    Set<Long> pointIds = new HashSet<>();

    private Double dist;

    Set<Integer> fractions = new HashSet<>();

    @Override
    public String getBotUsername() { return config.getBotUsername(); }

    @Override
    public String getBotToken() { return config.getToken(); }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if(update.hasMessage()) {
            // Message message = update.getMessage();
            String messageText = null;
            long chatId = update.getMessage().getChatId();
            String memberName = update.getMessage().getFrom().getFirstName();
            dist = null;
            Location location = update.getMessage().getLocation();
            if (update.getMessage().hasLocation()) {
                location = update.getMessage().getLocation();
                lon = location.getLongitude();
                lat = location.getLatitude();
                try {
                    pointIds = pointController.getRecByDefault(lon, lat);
                    List<PointPayload> pointPayloads = pointController.getPayloadForUser(pointIds);
                    for (PointPayload pointPayload : pointPayloads) {
                        SendMessage message = new SendMessage();
                        message.setChatId(update.getMessage().getChatId());
                        message.setText(pointPayload.getTitle() + "\n" + pointPayload.getUrl() + "\n" + "\n" + pointPayload.getAddress()
                                + "\n" + pointPayload.getFractions());
                        try {
                            execute(message);
                            log.info("Reply sent");
                        } catch (TelegramApiException e) {
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
            log.info("lon, lat " + lon + " " + lat);
            if (update.getMessage().hasText()) {
                messageText = update.getMessage().getText();
                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        getLocation(chatId);
                        break;
                    case "/subscribe":
                        log.info("Subscribe typed");
                        try {
                            saveSubscribe(chatId, lon, lat, dist, fractions, pointIds);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "/setDistance":
                        getInlineMessageButtonsForDistance(chatId);
                        // saveSubcribe()
                        break;
                    case "/setFractions":
                        getInlineMessageButtonsForFractions(chatId);
                        // saveSubcribe()
                        break;
               /* case "/location":
                    getLocation(chatId);
                    break;*/
                    default:
                        log.info("Unexpected message");
                }
            }
        } else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            Long chat_id = update.getCallbackQuery().getMessage().getChatId();
            SendMessage messageAfter = new SendMessage();
            String answer;
            switch(call_data){
                case ("5_минут"):
                    answer = "chosen 5 minutes";
                    dist = 5*60*1.43;
                    log.info("Dist" + dist);
                    messageAfter.setChatId(chat_id);
                    messageAfter.setText(answer);
                    try {
                        execute(messageAfter);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case ("15_минут"):
                    answer = "chosen 15 minutes";
                    dist = 15*60*1.43;
                    log.info("Dist" + dist);
                    messageAfter.setChatId(chat_id);
                    messageAfter.setText(answer);
                    try {
                        execute(messageAfter);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case ("30_минут"):
                    answer = "chosen 30 minutes";
                    dist = 30*60*1.43;
                    log.info("Dist" + dist);
                    messageAfter.setChatId(chat_id);
                    messageAfter.setText(answer);
                    try {
                        execute(messageAfter);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case ("1_км"):
                    answer = "chosen 1 km";
                    dist = 1000.00;
                    log.info("Dist" + dist);
                    messageAfter.setChatId(chat_id);
                    messageAfter.setText(answer);
                    try {
                        execute(messageAfter);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case ("5_км"):
                    answer = "chosen 5 km";
                    dist = 5000.00;
                    log.info("Dist" + dist);
                    messageAfter.setChatId(chat_id);
                    messageAfter.setText(answer);
                    try {
                        execute(messageAfter);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case ("10_км"):
                    answer = "chosen 10 km";
                    dist = 10000.00;
                    log.info("Dist" + dist);
                    messageAfter.setChatId(chat_id);
                    messageAfter.setText(answer);
                    try {
                        execute(messageAfter);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case ("BUMAGA"):
                    answer = "выбрали бумагу";
                    fractions.add(1);
                    log.info("Fractions " + fractions);
                    messageAfter.setChatId(chat_id);
                    messageAfter.setText(answer);
                    try {
                        execute(messageAfter);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case ("PLASTIK"):
                    answer = "выбрали пластик";
                    fractions.add(2);
                    log.info("Fractions " + fractions);
                    messageAfter.setChatId(chat_id);
                    messageAfter.setText(answer);
                    try {
                        execute(messageAfter);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    dist = null;
            }

            }
        }

        /*else if (update.getMessage().hasLocation()) {
           *//* String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();*//*
           *//* if (callbackData.equals("location")) {*//*
            ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove(true);
            Location location = update.getMessage().getLocation();
            Double lon = location.getLongitude();
            Double lat = location.getLatitude();
            try {
                Set<Long> pointIds =  pointController.getRecByDefault(lon, lat);
                List<PointPayload> pointPayloads = pointController.getPayloadForUser(pointIds);
                for(PointPayload pointPayload: pointPayloads){
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId());
                    message.setText(pointPayload.getTitle()+"\n"+ pointPayload.getUrl()+"\n"+ "\n"+pointPayload.getAddress()
                    + "\n" + pointPayload.getFractions());
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
        }*/

   /* private void saveSubscribe(Long chatId, Location location){
        Double lon = location.getLongitude();
        Double lat = location.getLatitude();
    }*/

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

    private void saveSubscribe(Long chatId, Double lon, Double lat, Double dist, Set<Integer> fractions, Set<Long> pointIds) throws IOException {
        this.subscribeController.saveSubscribe(chatId, lon, lat, dist, fractions, pointIds);
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


    private void getInlineMessageButtonsForDistance(Long chat_id) {
        SendMessage message = new SendMessage();
        message.setChatId(chat_id);
        message.setText("You send /setDistance");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
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
        rowInline.add(inlineKeyboardButton);
        rowInline.add(inlineKeyboardButton1);
        rowInline.add(inlineKeyboardButton2);
        rowInline2.add(inlineKeyboardButton3);
        rowInline2.add(inlineKeyboardButton4);
        rowInline2.add(inlineKeyboardButton5);
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);

        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getInlineMessageButtonsForFractions(Long chat_id) {
        SendMessage message = new SendMessage();
        message.setChatId(chat_id);
        message.setText("You send /setFractions");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
       // List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Бумага");
        inlineKeyboardButton.setCallbackData("BUMAGA");
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Пластик");
        inlineKeyboardButton1.setCallbackData("PLASTIK");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Стекло");
        inlineKeyboardButton2.setCallbackData("STEKLO");
        /*InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("1 км");
        inlineKeyboardButton3.setCallbackData("1_км");
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText("5 км");
        inlineKeyboardButton4.setCallbackData("5_км");
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        inlineKeyboardButton5.setText("10 км");
        inlineKeyboardButton5.setCallbackData("10_км");*/
        rowInline.add(inlineKeyboardButton);
        rowInline.add(inlineKeyboardButton1);
        rowInline.add(inlineKeyboardButton2);
        /*rowInline2.add(inlineKeyboardButton3);
        rowInline2.add(inlineKeyboardButton4);
        rowInline2.add(inlineKeyboardButton5);*/
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
     //   rowsInline.add(rowInline2);

        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
            user.setUserName(chat.getUserName());

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }

    private final String ECOADVICES_URL = "https://recyclemap-api-master.rc.geosemantica.ru/public/ecoadvices/random";


   // @Scheduled(fixedDelay = 1200000)
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


  //  @Scheduled(fixedDelay = 120000)
    public void updateDatabase() throws IOException {
        this.fractionService.save();
        this.pointService.save();
        //delete from points_fractions where point_id in (select point_id
        //	from points_history
        //	where updated = false and upper(valid_range) is NULL
        //				and (now() - INTERVAL '2400 seconds') > lower(valid_range) and point_id < 10);
        //обновляем подписки
        List<Long> deletedIds = this.pointService.getDeleted();
        if (!deletedIds.isEmpty()) {
            for (Long id: deletedIds) {
                this.pointService.delete(id);
            }
        }
        List<Long> subscribeIds = this.subscribeRepository.getAllSubscribeIds();
        for (Long subscribeId: subscribeIds) {
                Set<String> fractionIds = this.fractionService.getFractionIdsBySubscribeId(subscribeId);
                SubscribeString subscribeString = this.subscribeRepository.getSubscribeById(subscribeId);
                Set<Long> currentPoints = this.pointService.getPointsBySubscribeId(subscribeId);
                Point point = GeometryUtil.parseLocation(subscribeString.getLocation());
                Set<Long> newPoints = this.pointService.getRec(point.getX(), point.getY(), subscribeString.getDist(), fractionIds);
                Long chatId = subscribeString.getChatId();
                if (!Objects.equals(currentPoints, newPoints)) {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("test");
                    log.info("Found changes");
                    try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                log.info("Found changes");
                }

        }
        //обновляем подписки
    }

