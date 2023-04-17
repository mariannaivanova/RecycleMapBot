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
        URL url = new URL("https://recyclemap-api-master.rc.geosemantica.ru/public/fractions");
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = (ArrayNode) mapper.readTree(url).get("data");
        for(JsonNode jsonNode : arrayNode) {
            FractionDTO fractionDTO = new FractionDTO(jsonNode.get("id").asInt(), jsonNode.get("name").asText(), jsonNode.get("color").asText());
            Fraction fraction = fractionDTO.FractionDTOtoFraction();
            this.fractionService.save(fraction);
        }
    }

}
