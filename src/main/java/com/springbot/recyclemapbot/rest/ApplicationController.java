package com.springbot.recyclemapbot.rest;

import com.springbot.recyclemapbot.DTO.ApplicationDTO;
import com.springbot.recyclemapbot.payload.ApplicationPayload;
import com.springbot.recyclemapbot.serviceImplementation.ApplicationServiceImpl;
import com.springbot.recyclemapbot.serviceImplementation.FractionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationServiceImpl applicationService;

    private final FractionServiceImpl fractionService;

    @RequestMapping(value = "/application/{chatId}", method = RequestMethod.POST)
    public void saveApplication(@PathVariable("chatId") Long chatId, @RequestBody Double lon, Double lat, String title, Set<String> fractions) throws IOException {
        this.applicationService.save(chatId, lon, lat, title, fractions);
    }

    @RequestMapping(value = "/applicationsForUser", method = RequestMethod.GET)
    public List<ApplicationPayload> getApplicationsForUser(@RequestParam Long chatId) throws IOException {
        List<ApplicationPayload> applicationPayloads = new ArrayList<>();
        List<ApplicationDTO> applicationDTOs = this.applicationService.getApplicationInfo(chatId);
        for (ApplicationDTO applicationDTO: applicationDTOs){
            Set<String> fractions = this.fractionService.getFractionIdsByApplicationId(applicationDTO.getId());
            ApplicationPayload applicationPayload = new ApplicationPayload(applicationDTO, fractions);
            applicationPayloads.add(applicationPayload);
        }
        return applicationPayloads;
    }
}
