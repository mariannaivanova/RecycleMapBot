package com.springbot.recyclemapbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbot.recyclemapbot.payload.ApplicationPayload;
import com.springbot.recyclemapbot.payload.PointPayload;
import com.springbot.recyclemapbot.payload.SubscribePayload;
import com.springbot.recyclemapbot.DTO.SubscribeString;
import com.springbot.recyclemapbot.model.User;
import com.springbot.recyclemapbot.repository.SubscribeRepository;
import com.springbot.recyclemapbot.repository.UserRepository;
import com.springbot.recyclemapbot.rest.ApplicationController;
import com.springbot.recyclemapbot.rest.PointController;
import com.springbot.recyclemapbot.rest.SubscribeController;
import com.springbot.recyclemapbot.serviceImplementation.FractionServiceImpl;
import com.springbot.recyclemapbot.serviceImplementation.PointServiceImpl;
import com.springbot.recyclemapbot.serviceImplementation.SubscribeServiceImpl;
import jakarta.validation.constraints.NotNull;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
@EnableAsync
@Setter
@Getter
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String help = "Здесь вы можете ознакомиться с основными командами бота:\n\n" +
             "/createSubscribe команда для создания новой подписки\n\n" +
            "/getSubscribes команда для просмотра всех ваших подписок\n\n" +
            "/application команда для создания заявки на новую точку раздельного сбора мусора\n\n" +
            "/getApplications команда для просмотра всех ваших заявок\n\n";

    private final UserRepository userRepository;

    private final SubscribeRepository subscribeRepository;

    private final PointController pointController;

    private final SubscribeController subscribeController;

    private final FractionServiceImpl fractionService;

    private final PointServiceImpl pointService;

    private final BotConfig config;

    private final MenuService menuService;

    private final SubscribeServiceImpl subscribeService;

    private Double lon;

    private Double lat;

    private final ApplicationController applicationController;

    Set<Long> pointIds = new HashSet<>();

    private Double dist;

    Set<String> fractions = new HashSet<>();

    BotState botState;

    String title;

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
            // String memberName = update.getMessage().getFrom().getFirstName();
            //Location location = update.getMessage().getLocation();
            if (update.getMessage().hasLocation()) {
                dist = null;
                Location location = update.getMessage().getLocation();
                lon = location.getLongitude();
                lat = location.getLatitude();
                log.info("Bot state " + botState.toString());
                switch (botState.toString()) {
                    case "SUBSCRIBE":
                        try {
                            pointIds = pointController.getWithParameters(lon, lat, dist, fractions);
                            List<PointPayload> pointPayloads = pointController.getPayloadForUser(pointIds);
                            for (PointPayload pointPayload : pointPayloads) {
                                String answer = pointPayload.getTitle() + "\n" + pointPayload.getUrl() + "\n" + "\n" + pointPayload.getAddress()
                                        + "\n" + pointPayload.toNornalNames();
                                sendMessage(chatId, answer);
                            }
                            sendMessage(chatId, "Мы нашли ближайшие пять точек для сбора: <b>пластика</b>, <b>стекла</b>, <b>батареек</b> и <b>лампочек</b>.\n\nЧтобы подписаться на обновления этих точек, нажмите /subscribe\n\nЧтобы настроить поиск нажмите /setDistance");
                            log.info("Reply sent /subscribe");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // subscribeRepository.saveSubscribe(update.getMessage().getChatId(), lon, lat);
                        log.info("Location" + location);
                        break;
                    case "APPLICATION":
                        sendMessage(chatId, "Нажмите /setFractions чтобы выбрать желаемые фракции");
                        log.info("Location" + location);
                    default:
                        //throw new IllegalStateException("Unexpected value: " + botState);
                }

            }
            log.info("lon, lat " + lon + " " + lat);
            if (update.getMessage().hasText()) {
                messageText = update.getMessage().getText();
                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        try {
                            execute(this.menuService.getMainMenuKeyboard(chatId));
                            log.info("Reply sent");
                        } catch (TelegramApiException e){
                            log.error(e.getMessage());
                        }
                        botState = BotState.SUBSCRIBE;
                        break;
                    case "/createSubscribe":
                        botState = BotState.SUBSCRIBE;
                        sendMessage(chatId, "В моем арсенале много точек, отправь геолокацию места, где будем искать точки.\n\nДля этого нажми на скрепку -> значок геолокации -> отправить эту локацию");
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
                        fractions.clear();
                        getInlineMessageButtonsForFractions(chatId, botState);
                        // saveSubcribe()
                        break;
                    case "/search":
                        if (lon == null || lat == null){
                            getLocation(chatId);
                        } else {
                            try {
                                log.info("kkkk" + lon + lat + dist + fractions);
                                pointIds = pointController.getWithParameters(lat, lon, dist, fractions);
                                log.info("tochki" + pointIds);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            List<PointPayload> pointPayloads = null;
                            try {
                                pointPayloads = pointController.getPayloadForUser(pointIds);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (pointPayloads.isEmpty()) {
                                String answer = "К сожалению, по данному запросу точки не найдены, попробуйте поменять настройки поиска /setDistance";
                                sendMessage(chatId, answer);
                            }
                            for (PointPayload pointPayload : pointPayloads) {
                                String answer = pointPayload.getTitle() + "\n" + pointPayload.getUrl() + "\n" + "\n" + pointPayload.getAddress()
                                        + "\n" + pointPayload.toNornalNames();
                                sendMessage(chatId, answer);
                            }
                            sendMessage(chatId, "Нажмите /subscribe или настройте поиск заново /setDistance");
                            log.info("Reply sent /subscribe");
                        }
                        break;
                    case "/application":
                        botState = BotState.APPLICATION;
                        sendMessage(chatId, "Прикрепите геопозицию того места, в котором хотите видеть точку сбора.\n\nДля этого нажми на скрепку -> значок геолокации -> отправить эту локацию");
                        break;
                    case "/setTitle":
                        sendMessage(chatId, "Введите название своей подписки");
                        log.info("fractions for application", fractions);
                        break;
                    case "/help":
                        sendMessage(chatId, help);
                        break;
                    case "/getApplications":
                        try {
                            List<ApplicationPayload> applicationPayloads = this.applicationController.getApplicationsForUser(chatId);
                            for (ApplicationPayload applicationPayload: applicationPayloads){
                                sendMessage(chatId, applicationPayload.getText());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "/getSubscribes":
                        try {
                            List<SubscribePayload> subscribePayloads = this.subscribeController.getSubscribesForUser(chatId);
                            for (SubscribePayload subscribePayload: subscribePayloads){
                                SendMessage message = new SendMessage();
                                message.setChatId(chatId);
                                message.setReplyMarkup(getInlineMessageButtonsForFractions(chatId, subscribePayload.getId()));
                                message.setText(subscribePayload.getText());
                                try {
                                    execute(message);
                                } catch (TelegramApiException e){
                                    log.error(e.getMessage());
                                }
                                log.info("sbsc pl" + subscribePayload.getId());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    default:
                        switch(botState.toString()){
                            case "APPLICATION":
                                try {
                                    title = messageText;
                                    log.info("fractions for application", fractions);
                                    this.applicationController.saveApplication(chatId, lon, lat, title, fractions);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
               /* case "/location":
                    getLocation(chatId);
                    break;*/
                }
            }
        } else if (update.hasCallbackQuery()) {
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String answer;
            switch(call_data){
                case ("5_минут"):
                    answer = "Вы выбрали точки в радиусе 5 минут ходьбы\n Нажмите /setFractions чтобы выбрать фракции";
                    dist = 5*60*1.43;
                    log.info("Dist" + dist);
                    sendMessage(chatId, answer);
                    break;
                case ("15_минут"):
                    answer = "Вы выбрали точки в радиусе 15 минутах ходьбы\n Нажмите /setFractions чтобы выбрать фракции";;
                    dist = 15*60*1.43;
                    log.info("Dist" + dist);
                    sendMessage(chatId, answer);
                    break;
                case ("30_минут"):
                    answer = "Вы выбрали точки в радиусе 30 минут ходьбы\n Нажмите /setFractions чтобы выбрать фракции";;
                    dist = 30*60*1.43;
                    log.info("Dist" + dist);
                    sendMessage(chatId, answer);
                    break;
                case ("1_км"):
                    answer = "Вы выбрали точки в радиусе 1 км ходьбы\n Нажмите /setFractions чтобы выбрать фракции";
                    dist = 1000.00;
                    log.info("Dist" + dist);
                    sendMessage(chatId, answer);
                    break;
                case ("5_км"):
                    answer = "Вы выбрали точки в радиусе 5 км ходьбы\n Нажмите /setFractions чтобы выбрать фракции";;
                    dist = 5000.00;
                    log.info("Dist" + dist);
                    sendMessage(chatId, answer);
                    break;
                case ("10_км"):
                    answer = "Вы выбрали точки в радиусе 6 км ходьбы\n Нажмите /setFractions чтобы выбрать фракции";
                    dist = 10000.00;
                    log.info("Dist" + dist);
                    sendMessage(chatId, answer);
                    break;
                case ("closest"):
                    answer = "Вы выбрали ближайшие точки\nНажмите /setFractions чтобы выбрать фракции";
                    dist = null;
                    log.info("Dist" + dist);
                    sendMessage(chatId, answer);
                    break;
                case ("BUMAGA"):
                    answer = "выбрали бумагу";
                    fractions.add("BUMAGA");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("PLASTIK"):
                    answer = "выбрали пластик";
                    fractions.add("PLASTIK");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("STEKLO"):
                    answer = "выбрали стекло";
                    fractions.add("STEKLO");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("METALL"):
                    answer = "выбрали металл";
                    fractions.add("METALL");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("TETRA_PAK"):
                    answer = "выбрали тетра пак";
                    fractions.add("TETRA_PAK");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("ODEZHDA"):
                    answer = "выбрали одежда";
                    fractions.add("ODEZHDA");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("LAMPOCHKI"):
                    answer = "выбрали лампочки";
                    fractions.add("LAMPOCHKI");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("KRYSHECHKI"):
                    answer = "выбрали крышечки";
                    fractions.add("KRYSHECHKI");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("BYTOVAJA_TEHNIKA"):
                    answer = "выбрали бытовая техника";
                    fractions.add("BYTOVAJA_TEHNIKA");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("BATAREJKI"):
                    answer = "выбрали батарейки";
                    fractions.add("BATAREJKI");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("SHINY"):
                    answer = "выбрали шины";
                    fractions.add("SHINY");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("OPASNYE_OTHODY"):
                    answer = "выбрали опасные отходы";
                    fractions.add("OPASNYE_OTHODY");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("INOE"):
                    answer = "выбрали иное";
                    fractions.add("INOE");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                default:
                    if (call_data.contains("Айди подписки")){
                        log.info(call_data);
                        Pattern pat=Pattern.compile("[-]?[0-9]+(.[0-9]+)?");
                        Matcher matcher=pat.matcher(call_data);
                        while (matcher.find()) {
                            try {
                                this.subscribeController.deleteSubscribe(Long.parseLong(matcher.group(0)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            log.info(matcher.group(0));
                        };
                    }
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

    private void saveSubscribe(Long chatId, Double lon, Double lat, Double dist, Set<String> fractions, Set<Long> pointIds) throws IOException {
        this.subscribeController.saveSubscribe(chatId, lon, lat, dist, fractions, pointIds);
    }

    private void sendMessage(Long chatId, String text){
        SendMessage message = new SendMessage();
        message.setParseMode(ParseMode.HTML);
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
            log.info("Reply sent" + text);
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


    private void getInlineMessageButtonsForDistance(Long chatId) {
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
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getInlineMessageButtonsForFractions(Long chatId, Long subscribeId) {
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


    private void getInlineMessageButtonsForFractions(Long chatId, BotState botState) {
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
        inlineKeyboardButton.setText("Бумага");
        inlineKeyboardButton.setCallbackData("BUMAGA");
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Пластик");
        inlineKeyboardButton1.setCallbackData("PLASTIK");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Стекло");
        inlineKeyboardButton2.setCallbackData("STEKLO");
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Металл");
        inlineKeyboardButton3.setCallbackData("METALL");
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText("Тетра пак");
        inlineKeyboardButton4.setCallbackData("TETRA_PAK");
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        inlineKeyboardButton5.setText("Одежда");
        inlineKeyboardButton5.setCallbackData("ODEZHDA");
        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();
        inlineKeyboardButton6.setText("Лампочки");
        inlineKeyboardButton6.setCallbackData("LAMPOCHKI");
        InlineKeyboardButton inlineKeyboardButton7 = new InlineKeyboardButton();
        inlineKeyboardButton7.setText("Крышечки");
        inlineKeyboardButton7.setCallbackData("KRYSHECHKI");
        InlineKeyboardButton inlineKeyboardButton8 = new InlineKeyboardButton();
        inlineKeyboardButton8.setText("Бытовая техника");
        inlineKeyboardButton8.setCallbackData("BYTOVAJA_TEHNIKA");
        InlineKeyboardButton inlineKeyboardButton9 = new InlineKeyboardButton();
        inlineKeyboardButton9.setText("Батарейки");
        inlineKeyboardButton9.setCallbackData("BATAREJKI");
        InlineKeyboardButton inlineKeyboardButton10 = new InlineKeyboardButton();
        inlineKeyboardButton10.setText("Шины");
        inlineKeyboardButton10.setCallbackData("SHINY");
        InlineKeyboardButton inlineKeyboardButton11 = new InlineKeyboardButton();
        inlineKeyboardButton11.setText("Опасные отходы");
        inlineKeyboardButton11.setCallbackData("OPASNYE_OTHODY");
        InlineKeyboardButton inlineKeyboardButton12 = new InlineKeyboardButton();
        inlineKeyboardButton12.setText("Иное");
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


    @Scheduled(fixedDelay = 1200000)
    public void sendEcoAdvice() throws IOException {
        URL url = new URL(ECOADVICES_URL);
        ObjectMapper mapper = new ObjectMapper();
        String advice = "Экосовет от бота:\n" + mapper.readTree(url).get("data").get("advice").asText().replaceAll("<a href=\"", "").replaceAll("\" target=\"_blank\">", " ").replaceAll("\\<.*?\\>", "");
        List<Long> chatIds = this.userRepository.getAllChatIds();
        for(Long chatId : chatIds){
            sendMessage(chatId, advice);
        }

    }


    //@Scheduled(fixedDelay = 300000)
    public void updateDatabase() throws IOException {
        this.fractionService.save();
        this.pointService.save();
        List<Long> deletedIds = this.pointService.getDeleted();
        if (!deletedIds.isEmpty()) {
            for (Long id : deletedIds) {
                this.pointService.delete(id);
            }
        }
        List<Long> subscribeIds = this.subscribeRepository.getAllSubscribeIds();
        for (Long subscribeId : subscribeIds) {
            Set<String> fractionIds = this.fractionService.getFractionIdsBySubscribeId(subscribeId);
            SubscribeString subscribeString = this.subscribeRepository.getSubscribeById(subscribeId);
            log.info("SubscribeString" + subscribeString.getChatId() + subscribeString.getLocation()+ subscribeString.getDistance());
            Set<Long> currentPoints = this.pointService.getPointsBySubscribeId(subscribeId);
            Point point = GeometryUtil.parseLocation(subscribeString.getLocation());
            Set<Long> newPoints = new HashSet<>();
            Boolean updates = this.pointService.checkUpdates(currentPoints);
            log.info("jjjj" + updates);
            newPoints = this.pointService.getRec(point.getX(), point.getY(), subscribeString.getDistance(), fractionIds);
            Long chatId = subscribeString.getChatId();
            if (!Objects.equals(currentPoints, newPoints) || updates) {
                this.subscribeService.deleteSubscribePoint(subscribeId);
                this.subscribeService.saveSubscribePoint(subscribeId, newPoints);
                List<PointPayload> pointPayloads = pointController.getPayloadForUser(newPoints);
                for (PointPayload pointPayload : pointPayloads) {
                    String answer = pointPayload.getTitle() + "\n" + pointPayload.getUrl() + "\n" + "\n" + pointPayload.getAddress()
                            + "\n" + pointPayload.toNornalNames();
                    sendMessage(chatId, answer);
                }
                sendMessage(chatId, "Проверьте, какие изменения произошли в вашей подписке!");
                // send message подумать как разнести два критерия
                log.info("Found changes");
            } else {
                log.info("No changes");
            }
        }
        this.pointService.setUpdated(false);
    }
    }

