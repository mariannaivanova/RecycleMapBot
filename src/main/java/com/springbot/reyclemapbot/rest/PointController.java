package com.springbot.reyclemapbot.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.springbot.reyclemapbot.DTO.PointDTO;
import com.springbot.reyclemapbot.model.Points;
import com.springbot.reyclemapbot.repository.PointRepository;
import com.springbot.reyclemapbot.serviceImplementation.PointServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PointController {

    private final PointServiceImpl pointService;


    @RequestMapping(value = "/points", method = RequestMethod.GET)
    /*public String getJson() throws IOException {
        URL url = new URL("https://recyclemap-api-master.rc.geosemantica.ru/public/ecoadvices/random");
        String json = IOUtils.toString(url, StandardCharsets.UTF_8);
        return json;
    }*/
    public List<PointDTO> get() throws IOException {
        URL url = new URL("https://recyclemap-api-master.rc.geosemantica.ru/public/points?bbox=-180,-89,180,89");
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = (ArrayNode) mapper.readTree(url).get("data").get("points");
        List<PointDTO> pointDTOList = new ArrayList<PointDTO>();
        for(JsonNode jsonNode : arrayNode) {
            PointDTO pointDTO = new PointDTO();
            pointDTO.setPointId(jsonNode.get("pointId").asInt());
            String[] words = jsonNode.get("geom").asText().replaceAll("[\\()a-zA-Z]", "").split(" ");
            List<Double> coordinates = new ArrayList<Double>();
            for (String word: words){
                double d=Double.parseDouble(word);
                coordinates.add(d);
            }
            pointDTO.setX(coordinates.get(0));
            pointDTO.setY(coordinates.get(1));
            pointDTO.setAddress(jsonNode.get("address").asText());
            pointDTO.setTitle(jsonNode.get("title").asText());
            pointDTO.setRestricted(jsonNode.get("restricted").asBoolean());
            Points point = pointDTO.PointDTOToPoints();
            this.pointService.save(jsonNode.get("pointId").asLong(), pointDTO.getAddress(), pointDTO.getRestricted(), pointDTO.getTitle(), pointDTO.getX(), pointDTO.getY());
            pointDTOList.add(pointDTO);
        }
        return pointDTOList;
    }

}
