package com.springbot.reyclemapbot.serviceImplementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.springbot.reyclemapbot.DTO.Helper;
import com.springbot.reyclemapbot.config.GeometryUtil;
import com.springbot.reyclemapbot.model.Points;
import com.springbot.reyclemapbot.repository.PointFractionRepository;
import com.springbot.reyclemapbot.repository.PointRepository;
import com.springbot.reyclemapbot.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.geo.Point;
import com.vividsolutions.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;

    private final String POINTS_URL = "https://new.recyclemap.ru/api/public/points?bbox=";

    private final String POINT_VIEW_URL = "https://new.recyclemap.ru/viewer/points/";

    private final PointFractionRepository pointFractionRepository;

    @Override
    public void save() throws IOException {
        URL url = new URL(POINTS_URL+ "-180,-89,180,89");
        ObjectMapper mapper = new ObjectMapper();
        Integer pointsNumber = mapper.readTree(url).get("data").get("totalResults").asInt();
        Integer i = 0;

        while (i <= 100) {
            URL curUrl = new URL(POINTS_URL + "-180,-89,180,89"+ "&size=100" + "&offset=" + i);
            ArrayNode arrayNode = (ArrayNode) mapper.readTree(curUrl).get("data").get("points");
            for (JsonNode jsonNode : arrayNode) {
                Points point = new Points();
                point.setId(jsonNode.get("pointId").asLong());
                /*
                List<Double> coordinates = new ArrayList<Double>();
                for (String word : words) {
                    double d = Double.parseDouble(word);
                    coordinates.add(d);
                }
                pointDTO.setX(coordinates.get(0));
                pointDTO.setY(coordinates.get(1));*/
                ArrayNode fractions = (ArrayNode) jsonNode.get("fractions");
                point.setAddress(jsonNode.get("address").asText());
                point.setTitle(jsonNode.get("title").asText());
                point.setUrl(POINT_VIEW_URL + jsonNode.get("pointId"));
                point.setRestricted(jsonNode.get("restricted").asBoolean());
                Set<Integer> fractionSet = new HashSet<>();
                for (JsonNode jsonNode1 : fractions){
                    Integer id = jsonNode1.get("id").asInt();
                    fractionSet.add(id);
                }
                Point p = GeometryUtil.parseLocation(jsonNode.get("geom").asText());
                point.setGeom(p);
                log.info("IN PointService save {}", point.getId());
                pointRepository.save(point.getId(), point.getAddress(), point.getTitle(), point.getGeom().getX(), point.getGeom().getY(), point.getUrl(), point.getRestricted());
                log.info("IN PointService savePointFraction");
                pointFractionRepository.savePointFraction(point.getId(), fractionSet);
            }
            i += 100;
        }
    }

    @Override
    public List<Long> getRec(Double lon, Double lat, Double dist, Set<String> fractions) {
        return this.pointRepository.getRec(lon, lat, dist, fractions);
    }

    public List<Long> getRecByDefault(Double lon, Double lat){
        Double dist = 500.00;
        Set<String> fractions = new HashSet<>();
        fractions.add("BUMAGA");
        fractions.add("PLASTIK");
        fractions.add("STEKLO");
        fractions.add("LAMPOCHKI");
        List<Long> ids = new ArrayList<>();
        while ((dist <= 3000)&&(ids.size()< 5)){
            log.info("IN PointService getRec {}", dist);
            ids = this.pointRepository.getRec(lon, lat, dist, fractions);
            dist += 500;
        }
        if (ids.size() <= 5){
            ids = this.pointRepository.getClosest(lon, lat, fractions);
        };
        return ids;
    }

    @Override
    public void delete(Long id) {
        this.pointRepository.deleteById(id);
    }

    @Override
    public List<Long> getDeleted() {
        return this.pointRepository.getDeleted();
    }

    public List<Long> getPointsBySubscribeId(Long id){
        return this.pointRepository.getPointsBySubscribeId(id);
    }

}
