package com.springbot.recyclemapbot.rest;

import com.springbot.recyclemapbot.payload.SubscribePayload;
import com.springbot.recyclemapbot.DTO.SubscribeString;
import com.springbot.recyclemapbot.serviceImplementation.FractionServiceImpl;
import com.springbot.recyclemapbot.serviceImplementation.PointServiceImpl;
import com.springbot.recyclemapbot.serviceImplementation.SubscribeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeServiceImpl subscribeService;

    private final FractionServiceImpl fractionService;

    private final PointServiceImpl pointService;

    @RequestMapping(value = "/subscribe/{chatId}", method = RequestMethod.POST)
    public void saveSubscribe(@PathVariable("chatId") Long chatId, @RequestBody Double lon, Double lat, Double dist, Set<String> fractions, Set<Long> pointIds) throws IOException {
        this.subscribeService.save(chatId, lon, lat, dist, fractions, pointIds);
    }

    @RequestMapping(value = "/subscribe/{id}", method = RequestMethod.GET)
    public SubscribeString getSubscribeById(@PathVariable("id") Long id) throws IOException {
        return this.subscribeService.getSubscribeById(id);
    }

    @RequestMapping(value = "/subscribesForUser", method = RequestMethod.GET)
    public List<SubscribePayload> getSubscribesForUser(@RequestParam Long chatId) throws IOException {
        List<Long> subscribeIds = this.subscribeService.getSubscribesForUser(chatId);
        List<SubscribePayload> subscribePayloads = new ArrayList<>();
        for (Long subscribeId : subscribeIds){
            SubscribeString subscribeString = this.subscribeService.getSubscribeById(subscribeId);
            Set<String> fractions = this.fractionService.getFractionIdsBySubscribeId(subscribeId);
            List<String> points = this.pointService.getPointsAddress(subscribeId);
            subscribePayloads.add(new SubscribePayload(subscribeString, fractions, points));
        }
        return subscribePayloads;
    }

    @RequestMapping(value = "/subscribe/{id}", method = RequestMethod.DELETE)
    public void deleteSubscribe(@PathVariable("id") Long id) throws IOException {
        this.subscribeService.delete(id);
    }

}
