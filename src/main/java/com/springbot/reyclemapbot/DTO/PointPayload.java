package com.springbot.reyclemapbot.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class PointPayload {

    private String address;

    private String title;

    private String url;

    private Set<String> fractions;

    public PointPayload(PointDTO pointDTO, Set<String> fractions){
        this.address = pointDTO.getAddress();
        this.title = pointDTO.getTitle();
        this.url = pointDTO.getUrl();
        this.fractions = fractions;
    }
}
