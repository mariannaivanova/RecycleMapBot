package com.springbot.recyclemapbot.rest;

import com.springbot.recyclemapbot.DTO.PointDTO;
import com.springbot.recyclemapbot.payload.PointPayload;
import com.springbot.recyclemapbot.serviceImplementation.FractionServiceImpl;
import com.springbot.recyclemapbot.serviceImplementation.PointServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
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
    public List<PointPayload> getPayloadForUser(@RequestParam Set<Long> pointIds) throws IOException {
        List<PointPayload> pointPayloads = new ArrayList<>();
        for (Long pointId: pointIds){
            PointDTO pointDTO = this.pointService.getPointInfo(pointId);
            Set<String> fractions = this.fractionService.getFractionIdsByPointId(pointId);
            PointPayload   pointPayload = new PointPayload(pointDTO, fractions);
            pointPayloads.add(pointPayload);
        }
        return pointPayloads;
    }

   /* @RequestMapping(value = "/pointsForUser", method = RequestMethod.GET)
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
*/

   /* @RequestMapping(value = "/pointsRec", method = RequestMethod.GET)
    public List<Long> getRec(@RequestBody Helper helper) throws IOException {
        return this.pointService.getRec(helper);
    }*/


    @RequestMapping(value = "/pointsDefault", method = RequestMethod.GET)
    public Set<Long> getRecByDefault(@RequestParam Double lon, @RequestParam Double lat, @RequestParam Set<String> fractions) throws IOException {
        return this.pointService.getRecByDefault(lon, lat, fractions);
    }

    @RequestMapping(value = "/point/delete/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id) throws IOException {
        this.pointService.delete(id);
    }

    @RequestMapping(value = "/pointsWithParameters", method = RequestMethod.GET)
    public Set<Long> getWithParameters(@RequestParam Double lon, @RequestParam Double lat, @RequestParam Double dist, @RequestParam Set<String> fractionIds) throws IOException {
        return this.pointService.getRec(lon, lat, dist, fractionIds);
    }
}
