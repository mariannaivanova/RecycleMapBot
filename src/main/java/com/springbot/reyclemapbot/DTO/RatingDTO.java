package com.springbot.reyclemapbot.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingDTO {
    private Integer likes;
    private Integer dislikes;
    private Double score;

    public RatingDTO(){}
}
