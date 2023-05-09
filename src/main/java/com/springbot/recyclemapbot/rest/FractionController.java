package com.springbot.recyclemapbot.rest;

import com.springbot.recyclemapbot.serviceImplementation.FractionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class FractionController {

    private final FractionServiceImpl fractionService;

    @RequestMapping(value = "/fractions", method = RequestMethod.GET)
    public void saveFractions() throws IOException {
        this.fractionService.save();
    }

}
