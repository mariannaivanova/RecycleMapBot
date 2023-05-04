package com.springbot.reyclemapbot.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.springbot.reyclemapbot.DTO.Helper;
import com.springbot.reyclemapbot.DTO.PointDTO;
import com.springbot.reyclemapbot.config.GeometryUtil;
import com.springbot.reyclemapbot.model.Fraction;
import com.springbot.reyclemapbot.model.Points;
import com.springbot.reyclemapbot.repository.PointRepository;
import com.springbot.reyclemapbot.serviceImplementation.FractionServiceImpl;
import com.springbot.reyclemapbot.serviceImplementation.PointServiceImpl;
import com.vividsolutions.jts.geom.Point;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
public class PointController {

    private final PointServiceImpl pointService;

    private final FractionServiceImpl fractionService;
    private final String POINTS_URL = "https://new.recyclemap.ru/api/public/points?bbox=";

    private final String POINT_VIEW_URL = "https://recyclemap-ui-main.rc.geosemantica.ru/viewer/points/";

    @RequestMapping(value = "/points", method = RequestMethod.GET)
    public void get() throws IOException {
        this.pointService.save();
    }


    @RequestMapping(value = "/pointsForUser", method = RequestMethod.GET)
    public List<PointDTO> getRecommendation(@RequestParam Double lon, @RequestParam Double lat, @RequestParam Double dist) throws IOException {
        Double abs = Math.abs(Math.cos(Math.toRadians(lat)) * 111.0);
        double lon1 = lon - dist / abs;
        double lon2 = lon + dist / abs;
        double lat1 = lat - (dist / 111.0);
        double lat2 = lat + (dist / 111.0);
        //west south east north
        URL url = new URL(POINTS_URL + lon1 + "," + lat1 + "," + lon2 + "," + lat2 + "&offset=10");
        ObjectMapper mapper = new ObjectMapper();
        //  JsonNode jsonNode = mapper.readTree(url).get("data");
        //  Integer pointsNumber = mapper.readTree(url).get("data").get("totalResults").asInt();
        ArrayNode arrayNode = (ArrayNode) mapper.readTree(url).get("data").get("points");
        List<PointDTO> points = new ArrayList<>();
        for (JsonNode jsonNode : arrayNode) {
            PointDTO pointDTO = new PointDTO();
            pointDTO.setPointId(jsonNode.get("pointId").asInt());
            pointDTO.setAddress(jsonNode.get("address").asText());
            pointDTO.setTitle(jsonNode.get("title").asText());
            String[] words = jsonNode.get("geom").asText().replaceAll("[\\()a-zA-Z]", "").split(" ");
            pointDTO.setUrl(POINT_VIEW_URL + pointDTO.getId());
            points.add(pointDTO);
           // Points point = pointDTO.PointDTOToPoints();
          //  this.pointService.save(jsonNode.get("pointId").asLong(), pointDTO.getAddress(), pointDTO.getTitle(),Double.parseDouble(words[0]), Double.parseDouble(words[1]));

        }
        if (!points.isEmpty()){

        }
        return points;
    }


   /* @RequestMapping(value = "/pointsRec", method = RequestMethod.GET)
    public List<Long> getRec(@RequestBody Helper helper) throws IOException {
        return this.pointService.getRec(helper);
    }*/


    @RequestMapping(value = "/pointsDefault", method = RequestMethod.GET)
    public List<Long> getRecByDefault(@RequestParam Double lon, @RequestParam Double lat) throws IOException {
        return this.pointService.getRecByDefault(lon, lat);
    }

    @RequestMapping(value = "/point/delete/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id) throws IOException {
        this.pointService.delete(id);
    }
}
