package com.springbot.reyclemapbot.serviceImplementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.springbot.reyclemapbot.DTO.FractionDTO;
import com.springbot.reyclemapbot.model.Fraction;
import com.springbot.reyclemapbot.model.Points;
import com.springbot.reyclemapbot.repository.FractionRepository;
import com.springbot.reyclemapbot.repository.PointRepository;
import com.springbot.reyclemapbot.service.FractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FractionServiceImpl implements FractionService {

    private final FractionRepository fractionRepository;

    @Override
    public void save() throws IOException {
        URL url = new URL("https://new.recyclemap.ru/api/public/fractions");
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = (ArrayNode) mapper.readTree(url).get("data");
        for(JsonNode jsonNode : arrayNode) {
            FractionDTO fractionDTO = new FractionDTO(jsonNode.get("id").asInt(), jsonNode.get("name").asText(), jsonNode.get("color").asText());
            Fraction fraction = fractionDTO.FractionDTOtoFraction();
            this.fractionRepository.save(fraction);
        }
    }

    @Override
    public Fraction getFractionById(Integer id) {
        return this.fractionRepository.getFractionById(id);
    }

    public Set<String> getFractionIdsBySubscribeId(Long id){
        return this.fractionRepository.getFractionIdsBySubscribeId(id);
    }
}
