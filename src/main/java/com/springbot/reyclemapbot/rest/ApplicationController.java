package com.springbot.reyclemapbot.rest;

import com.springbot.reyclemapbot.serviceImplementation.ApplicationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationServiceImpl applicationService;

    @RequestMapping(value = "/application/{chatId}", method = RequestMethod.POST)
    public void saveApplication(@PathVariable("chatId") Long chatId, @RequestBody Double lon, Double lat, String title, Set<Integer> fractions) throws IOException {
        this.applicationService.save(chatId, lon, lat, title, fractions);
    }
}
