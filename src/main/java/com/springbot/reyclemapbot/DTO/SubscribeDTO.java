package com.springbot.reyclemapbot.DTO;

import com.springbot.reyclemapbot.model.Subscribe;
import com.springbot.reyclemapbot.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Location;

@Getter
@Setter
@RequiredArgsConstructor
public class SubscribeDTO {
    private Long chatId;

    private Double lon;

    private Double lat;

    private Double dist;

    public SubscribeDTO(Long chatId, Location location, Double dist){
        this.chatId = chatId;
        this.lon = location.getLongitude();
        this.lat = location.getLatitude();
        this.dist = dist;
    }

    public Subscribe subscribeDTOtoSubscribe(){
        Subscribe subscribe = new Subscribe();
        subscribe.setChatId(this.chatId);
        subscribe.setDist(this.dist);
        return subscribe;
    }


}
