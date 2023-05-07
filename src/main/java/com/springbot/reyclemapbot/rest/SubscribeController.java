package com.springbot.reyclemapbot.rest;

import com.springbot.reyclemapbot.DTO.SubscribeDTO;
import com.springbot.reyclemapbot.DTO.SubscribeString;
import com.springbot.reyclemapbot.DTO.UserDTO;
import com.springbot.reyclemapbot.model.Fraction;
import com.springbot.reyclemapbot.model.User;
import com.springbot.reyclemapbot.serviceImplementation.SubscribeServiceImpl;
import com.springbot.reyclemapbot.serviceImplementation.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeServiceImpl subscribeService;

    @RequestMapping(value = "/subscribe/{chatId}", method = RequestMethod.POST)
    public void saveSubscribe(@PathVariable("chatId") Long chatId, @RequestBody Double lon, Double lat, Double dist, Set<Integer> fractions, Set<Long> pointIds) throws IOException {
        this.subscribeService.save(chatId, lon, lat, dist, fractions, pointIds);
    }

    @RequestMapping(value = "/subscribe/{id}", method = RequestMethod.GET)
    public SubscribeString getSubscribeById(@PathVariable("id") Long id) throws IOException {
        return this.subscribeService.getSubscribeById(id);
    }

}
