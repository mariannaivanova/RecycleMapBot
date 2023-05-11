package com.springbot.recyclemapbot.rest;

import com.springbot.recyclemapbot.payload.PointPayload;
import com.springbot.recyclemapbot.DTO.SubscribeString;
import com.springbot.recyclemapbot.config.GeometryUtil;
//import com.springbot.reyclemapbot.config.HTMLParserUtil;
import com.springbot.recyclemapbot.repository.SubscribeRepository;
import com.springbot.recyclemapbot.repository.UserRepository;
import com.springbot.recyclemapbot.serviceImplementation.FractionServiceImpl;
import com.springbot.recyclemapbot.serviceImplementation.PointServiceImpl;
import com.springbot.recyclemapbot.serviceImplementation.SubscribeServiceImpl;
import com.vividsolutions.jts.geom.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class HelpController {
    private final UserRepository userRepository;

    private final SubscribeRepository subscribeRepository;

    private final PointController pointController;

    private final FractionServiceImpl fractionService;

    private final PointServiceImpl pointService;

    private final SubscribeServiceImpl subscribeService;

    // @Scheduled(cron = "${interval-in-cron}")
    @RequestMapping(value = "/updatedb", method = RequestMethod.POST)
    public void update() throws IOException {
        this.fractionService.save();
        this.pointService.save();
        List<Long> deletedIds = this.pointService.getDeleted();
        if (!deletedIds.isEmpty()) {
            for (Long id: deletedIds) {
                this.pointService.delete(id);
            }
        }
        List<Long> subscribeIds = this.subscribeRepository.getAllSubscribeIds();
        for (Long subscribeId: subscribeIds) {
            Set<String> fractions = this.fractionService.getFractionIdsBySubscribeId(subscribeId);
            SubscribeString subscribeString = this.subscribeRepository.getSubscribeById(subscribeId);
            Set<Long> currentPoints = this.pointService.getPointsBySubscribeId(subscribeId);
            Point point = GeometryUtil.parseLocation(subscribeString.getLocation());
            Set<Long> newPoints = new HashSet<>();
            Boolean updates = this.pointService.checkUpdates(currentPoints);
            log.info("jjjj"+ updates);
            if (subscribeString.getDistance() == null){
                newPoints = this.pointService.getRecByDefault(point.getX(), point.getY(), fractions);
            } else {
                newPoints = this.pointService.getRec(point.getX(), point.getY(), subscribeString.getDistance(), fractions);
            }
            Long chatId = subscribeString.getChatId();
            if (!Objects.equals(currentPoints, newPoints) || updates) {
                this.subscribeService.deleteSubscribePoint(subscribeId);
                this.subscribeService.saveSubscribePoint(subscribeId, newPoints);
                List<PointPayload> pointPayloads = pointController.getPayloadForUser(newPoints);
                for (PointPayload pointPayload : pointPayloads) {
                    String answer = pointPayload.getTitle() + "\n" + pointPayload.getUrl() + "\n" + "\n" + pointPayload.getAddress()
                            + "\n" + pointPayload.toNornalNames();
                    //sendMessage(chatId, answer);
                }
                //sendMessage(chatId, "Нажмите /subscribe или настройте поиск /setDistance");
                // send message подумать как разнести два критерия
                log.info("Found changes");
            } else {
                log.info("No changes");
            }
        }
        this.pointService.setUpdated(false);

    }

    @RequestMapping(value = "/parse", method = RequestMethod.POST)
    public String parse(){
        String text = "Подписывайтесь на <a href=\"https://act.greenpeace.org/page/63814/data/1?utm_source=recyclemap&utm_medium=referral&utm_campaign=plastic-leave2&ea.tracking.id=recyclemap-advise\" target=\"_blank\">рассылку Greenpeace</a> и получайте жизнеутверждающие и весёлые письма о том, как стать экологичным. Расскажем, как не утонуть в мусоре от доставки, сортировать отходы без шести дополнительных контейнеров на маленькой кухне и мыть голову, не загрязняя планету.";
        /*text.replaceAll("<a href=\"", "").replaceAll("\" target=\"_blank\">", "").replaceAll("\\<.*?\\>", "");
        // text.replaceAll("\\<.*?\\>", "")*/
        /*HTMLParserUtil htmlParserUtil = new HTMLParserUtil();
        String link = htmlParserUtil.getLinks(text).get(0);*/
        return text.replaceAll("\\<.*?\\>", "");
    }
}
