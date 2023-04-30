package com.springbot.reyclemapbot.serviceImplementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.springbot.reyclemapbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/*
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final UserRepository userRepository;

    private final String ECOADVICES_URL = "https://recyclemap-api-master.rc.geosemantica.ru/public/ecoadvices/random";

    @Scheduled(fixedRate = 3000)
    @Async
    public void sendEcoAdvice() throws IOException {
        URL url = new URL(ECOADVICES_URL);
        ObjectMapper mapper = new ObjectMapper();
        String advice = mapper.readTree(url).get("data").get("advice").asText();
        List<Long> chatIds = this.userRepository.getAllChatIds();
        for(Long chatId : chatIds){
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(advice);
            String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

            //Add Telegram token (given Token is fake)
            String apiToken = "6191684481:AAGRo7qaTRZSqW3tyBaDIMzo8p7AR65mFtY";

            //Add chatId (given chatId is fake)
            urlString = String.format(urlString, apiToken, chatId, advice);

            try {
                URL urlSend = new URL(urlString);
                URLConnection conn = url.openConnection();
                InputStream is = new BufferedInputStream(conn.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
*/
