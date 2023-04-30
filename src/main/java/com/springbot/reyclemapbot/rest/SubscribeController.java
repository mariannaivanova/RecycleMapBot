package com.springbot.reyclemapbot.rest;

import com.springbot.reyclemapbot.DTO.UserDTO;
import com.springbot.reyclemapbot.model.User;
import com.springbot.reyclemapbot.serviceImplementation.SubscribeServiceImpl;
import com.springbot.reyclemapbot.serviceImplementation.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeServiceImpl subscribeService;

    @RequestMapping(value = "/subscribe/{chatId}", method = RequestMethod.POST)
    public void saveSubscribe(@PathVariable("chatId") Long chatId, @RequestBody Location location, Double dist) throws IOException {
        this.subscribeService.save(chatId, location, dist);
    }

}
