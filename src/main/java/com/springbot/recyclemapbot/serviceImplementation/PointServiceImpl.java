package com.springbot.recyclemapbot.serviceImplementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.springbot.recyclemapbot.DTO.PointDTO;
import com.springbot.recyclemapbot.config.GeometryUtil;
import com.springbot.recyclemapbot.model.Points;
import com.springbot.recyclemapbot.repository.FractionRepository;
import com.springbot.recyclemapbot.repository.PointRepository;
import com.springbot.recyclemapbot.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.geo.Point;
import com.vividsolutions.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
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

    private final FractionRepository FractionRepository;



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
                ArrayNode fractions = (ArrayNode) jsonNode.get("fractions");
                point.setAddress(jsonNode.get("address").asText());
                point.setTitle(jsonNode.get("title").asText());
                point.setUrl(POINT_VIEW_URL + jsonNode.get("pointId"));
                point.setRestricted(jsonNode.get("restricted").asBoolean());
                Set<String> fractionSetOld = new HashSet<>();
                if (!this.FractionRepository.getFractionIdsByPointId(point.getId()).isEmpty()){
                    fractionSetOld = this.FractionRepository.getFractionIdsByPointId(point.getId());
                    log.info("old fractions: " + fractionSetOld);
                }
                Set<String> fractionSet = new HashSet<>();
                for (JsonNode jsonNode1 : fractions){
                    Integer id = jsonNode1.get("id").asInt();
                    String name = this.FractionRepository.getFractionById(id).getName();
                    fractionSet.add(name);
                }
                if (!fractionSetOld.isEmpty() && !fractionSetOld.equals(fractionSet)){
                    log.info("я тут");
                    this.pointRepository.setUpdatedByPointId(point.getId(), true);
                }
                Point p = GeometryUtil.parseLocation(jsonNode.get("geom").asText());
                point.setGeom(p);
                log.info("fractions new" + fractionSet);
                pointRepository.save(point.getId(), point.getAddress(), point.getTitle(), point.getGeom().getX(), point.getGeom().getY(), point.getUrl(), point.getRestricted(), fractionSet);
            }
            i += 100;
        }
    }

    @Override
    public Set<Long> getRec(Double lon, Double lat, Double dist, Set<String> fractions) {
        Set<Long> ids = new HashSet<>();
        if (dist == null){
            dist = 500.00;
            if (fractions.isEmpty()) {
                fractions.add("PLASTIK");
                fractions.add("STEKLO");
                fractions.add("LAMPOCHKI");
                fractions.add("BATAREJKI");
            }
            while ((dist <= 3000)&&(ids.size()< 5)){
                log.info("IN PointService getRec {}", dist);
                ids = this.pointRepository.getRec(lon, lat, dist, fractions);
                dist += 500;
            }
            if (ids.size() <= 5){
                ids = this.pointRepository.getClosest(lon, lat, fractions);
            };
        } else {
            ids = this.pointRepository.getRec(lon, lat, dist, fractions);
        }
        return ids;
    }

    public Set<Long> getRecByDefault(Double lon, Double lat, Set<String> fractions){
        Double dist = 500.00;
        if (fractions.isEmpty()) {
            fractions.add("PLASTIK");
            fractions.add("STEKLO");
            fractions.add("LAMPOCHKI");
            fractions.add("BATAREJKI");
        }
        Set<Long> ids = new HashSet<>();
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

    public Boolean checkUpdates(Set<Long> pointIds){
        List<Boolean> updates = this.pointRepository.checkUpdates(pointIds);
        Boolean answer = false;
        for (Boolean update: updates){
            if (update){
                answer = true;
                break;
            } else {
                answer = false;
            }
        }
        log.info("проверяем буллиан" + answer);
        return answer;
    }

    public PointDTO getPointInfo(Long id){
        return this.pointRepository.getPointInfo(id);
    }


    @Override
    public void delete(Long id) {
        this.pointRepository.deleteById(id);
    }

    @Override
    public List<Long> getDeleted() {
        return this.pointRepository.getDeleted();
    }

    public Set<Long> getPointsBySubscribeId(Long id){
        return this.pointRepository.getPointsBySubscribeId(id);
    }

    public void setUpdated(boolean updated){
        this.pointRepository.setUpdatedAll(updated);
    }

    public List<String> getPointsAddress(Long subscribeId){
        return this.pointRepository.getPointsAddress(subscribeId);
    }
}
