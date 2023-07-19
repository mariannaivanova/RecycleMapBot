package com.springbot.recyclemapbot.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbot.recyclemapbot.config.BotConfig;
import com.springbot.recyclemapbot.config.GeometryUtil;
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

    private final InlineMenuService inlineMenuService;

    private final SubscribeServiceImpl subscribeService;

    private Double lon;

    private Double lat;


//    Geocoder geocoder;
//    List<Address> addresses;
//    geocoder = new Geocoder(this, Locale.getDefault());
//
//    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//
//    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//    String city = addresses.get(0).getLocality();
//    String state = addresses.get(0).getAdminArea();
//    String country = addresses.get(0).getCountryName();
//    String postalCode = addresses.get(0).getPostalCode();
//    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

    private final ApplicationController applicationController;

    Set<Long> pointIds = new HashSet<>();

    private Double dist;

    Set<String> fractions = new HashSet<>();

    BotState botState;

    String title;

    public String toNormalNames(Set<String> fractions){
        Set<String> fractionsNormal = new HashSet<>();
        for (String fraction: fractions){
            switch(fraction){
                case ("BUMAGA"):
                    fractionsNormal.add("\uD83D\uDDDE бумага");
                    break;
                case ("PLASTIK"):
                    fractionsNormal.add("\uD83E\uDD64 пластик");
                    break;
                case ("STEKLO"):
                    fractionsNormal.add("\uD83E\uDED9 стекло");
                    break;
                case ("METALL"):
                    fractionsNormal.add("⛓ металл");
                    break;
                case ("TETRA_PAK"):
                    fractionsNormal.add("\uD83E\uDDC3 тетра пак");
                    break;
                case ("ODEZHDA"):
                    fractionsNormal.add("\uD83D\uDC55 одежда");
                    break;
                case ("LAMPOCHKI"):
                    fractionsNormal.add("\uD83D\uDCA1 лампочки");
                    break;
                case ("KRYSHECHKI"):
                    fractionsNormal.add("\uD83D\uDD73 крышечки");
                    break;
                case ("BYTOVAJA_TEHNIKA"):
                    fractionsNormal.add("\uD83C\uDF9B бытовая техника");
                    break;
                case ("BATAREJKI"):
                    fractionsNormal.add("\uD83D\uDD0B батарейки");
                    break;
                case ("SHINY"):
                    fractionsNormal.add("⚙️ шины");
                    break;
                case ("OPASNYE_OTHODY"):
                    fractionsNormal.add("⚠️ опасные отходы");
                    break;
                case ("INOE"):
                    fractionsNormal.add("\uD83E\uDD14 иное");
                    break;
                default:
            }
        }
        String f = "";
        for (String fraction : fractionsNormal) {
            f += fraction + "\n";
        }
        //return fractionsNormal.toString().replaceAll("\\[|\\]","");
        return f;
    }

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
                           // for (PointPayload pointPayload : pointPayloads) {
//                                String answer = "\uD83D\uDCCD" + pointPayload.getTitle() + "\n" + pointPayload.getUrl() + "\n" + "\n" + pointPayload.getAddress()
//                                        + "\n" + pointPayload.toNornalNames();
//                                sendMessage(chatId, answer);
                           // }
                            sendMessage(chatId, "Чтобы настроить поиск нажмите /setDistance");
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
                            int time = (int) (dist/(1.38*60));
                            String f = toNormalNames(fractions);
                            sendMessage(chatId, "Отлично! Теперь твоя подписка выглядит так: \n\nВремя в пути: \n" + time + " минут \n\n️ " + "Фракции: \n" + f);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "/setDistance":
                        try {
                            execute(inlineMenuService.getInlineMessageButtonsForDistance(chatId));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "/setFractions":
                        fractions.clear();
                        try {
                            execute(inlineMenuService.getInlineMessageButtonsForFractions(chatId, botState));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        // saveSubcribe()
                        break;
                    case "/search":
                        if (lon == null || lat == null){
                            getLocation(chatId);
                        } else {
                            try {
                                log.info("kkkk" + lon + lat + dist + fractions);
                                pointIds = pointController.getWithParameters(lon, lat, dist, fractions);
                                log.info("tochki" + pointIds);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            List<PointPayload> pointPayloads = new ArrayList<>();
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
                                int time = (int) (dist/(1.38*60));
                                String answer = "\uD83D\uDCCD" + pointPayload.getTitle() + "\n" + pointPayload.getUrl() + "\n" + "\n" + pointPayload.getAddress()
                                        + "\n"  + toNormalNames(pointPayload.getFractions()) + "\n Время в пути: " + time + " минут";
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
                                message.setReplyMarkup(inlineMenuService.getInlineMessageButtonsForSubscribe(chatId, subscribePayload.getId()));
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
                    answer = "Выбрали бумагу";
                    fractions.add("BUMAGA");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("PLASTIK"):
                    answer = "Выбрали пластик";
                    fractions.add("PLASTIK");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("STEKLO"):
                    answer = "Выбрали стекло";
                    fractions.add("STEKLO");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("METALL"):
                    answer = "Выбрали металл";
                    fractions.add("METALL");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("TETRA_PAK"):
                    answer = "Выбрали тетра пак";
                    fractions.add("TETRA_PAK");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("ODEZHDA"):
                    answer = "Выбрали одежда";
                    fractions.add("ODEZHDA");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("LAMPOCHKI"):
                    answer = "Выбрали лампочки";
                    fractions.add("LAMPOCHKI");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("KRYSHECHKI"):
                    answer = "Выбрали крышечки";
                    fractions.add("KRYSHECHKI");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("BYTOVAJA_TEHNIKA"):
                    answer = "Выбрали бытовая техника";
                    fractions.add("BYTOVAJA_TEHNIKA");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("BATAREJKI"):
                    answer = "Выбрали батарейки";
                    fractions.add("BATAREJKI");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("SHINY"):
                    answer = "Выбрали шины";
                    fractions.add("SHINY");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("OPASNYE_OTHODY"):
                    answer = "Выбрали опасные отходы";
                    fractions.add("OPASNYE_OTHODY");
                    log.info("Fractions " + fractions);
                    sendMessage(chatId, answer);
                    break;
                case ("INOE"):
                    answer = "Выбрали иное";
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


  //  @Scheduled(fixedDelay = 1200000)
    public void sendEcoAdvice() throws IOException {
        URL url = new URL(ECOADVICES_URL);
        ObjectMapper mapper = new ObjectMapper();
        String advice = "Экосовет от бота:\n" + mapper.readTree(url).get("data").get("advice").asText().replaceAll("<a href=\"", "").replaceAll("\" target=\"_blank\">", " ").replaceAll("\\<.*?\\>", "");
        List<Long> chatIds = this.userRepository.getAllChatIds();
        for(Long chatId : chatIds){
            sendMessage(chatId, advice);
        }

    }


    @Scheduled(cron = "${interval-in-cron}")
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

