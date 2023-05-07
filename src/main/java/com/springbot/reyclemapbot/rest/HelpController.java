package com.springbot.reyclemapbot.rest;

import com.springbot.reyclemapbot.DTO.SubscribeString;
import com.springbot.reyclemapbot.config.GeometryUtil;
import com.springbot.reyclemapbot.repository.SubscribeRepository;
import com.springbot.reyclemapbot.repository.UserRepository;
import com.springbot.reyclemapbot.serviceImplementation.FractionServiceImpl;
import com.springbot.reyclemapbot.serviceImplementation.PointServiceImpl;
import com.springbot.reyclemapbot.serviceImplementation.SubscribeServiceImpl;
import com.vividsolutions.jts.geom.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
            Set<String> fractionIds = this.fractionService.getFractionIdsBySubscribeId(subscribeId);
            SubscribeString subscribeString = this.subscribeRepository.getSubscribeById(subscribeId);
            Set<Long> currentPoints = this.pointService.getPointsBySubscribeId(subscribeId);
            Point point = GeometryUtil.parseLocation(subscribeString.getLocation());
            Set<Long> newPoints = new HashSet<>();
            Boolean updates = this.pointService.checkUpdates(currentPoints);
            if (subscribeString.getDist() == null){
                newPoints = this.pointService.getRecByDefault(point.getX(), point.getY());
            } else {
                newPoints = this.pointService.getRec(point.getX(), point.getY(), subscribeString.getDist(), fractionIds);
            }
            Long chatId = subscribeString.getChatId();
            if (!Objects.equals(currentPoints, newPoints) || updates) {
                this.subscribeService.deleteSubscribePoint(subscribeId);
                this.subscribeService.saveSubscribePoint(subscribeId, newPoints);
                // send message подумать как разнести два критерия
                log.info("Found changes");
            } else {
                log.info("No changes");
            }
        }
        this.pointService.setUpdatesFalse();

    }
}
