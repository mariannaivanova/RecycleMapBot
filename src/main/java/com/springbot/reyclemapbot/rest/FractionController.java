package com.springbot.reyclemapbot.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.springbot.reyclemapbot.DTO.FractionDTO;
import com.springbot.reyclemapbot.model.Fraction;
import com.springbot.reyclemapbot.serviceImplementation.FractionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;

@RequiredArgsConstructor
@RestController
public class FractionController {

    private final FractionServiceImpl fractionService;

    @RequestMapping(value = "/fractions", method = RequestMethod.GET)
    public void saveFractions() throws IOException {
        this.fractionService.save();
    }

}
