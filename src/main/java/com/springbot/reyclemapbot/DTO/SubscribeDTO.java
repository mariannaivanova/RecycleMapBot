package com.springbot.reyclemapbot.DTO;

import com.springbot.reyclemapbot.model.Subscribe;
import com.springbot.reyclemapbot.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SubscribeDTO {

    private Double lon;

    private Double lat;

    private Double dist;

    public SubscribeDTO(Location location, Double dist){
        this.lon = location.getLongitude();
        this.lat = location.getLatitude();
        this.dist = dist;
    }

    public SubscribeDTO(String location, Double dist){
        String[] words = location.replaceAll("[\\()a-zA-Z]", "").split(" ");
        List<Double> coordinates = new ArrayList<Double>();
        for (String word : words) {
            double d = Double.parseDouble(word);
            coordinates.add(d);
        }
        this.lon = coordinates.get(0);
        this.lat = coordinates.get(1);
        this.dist = dist;
    }

    public Subscribe subscribeDTOtoSubscribe(){
        Subscribe subscribe = new Subscribe();
        subscribe.setDist(this.dist);
        return subscribe;
    }


}
