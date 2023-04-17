package com.springbot.reyclemapbot.DTO;

import com.springbot.reyclemapbot.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SubscribeDTO {
    private Long chatId;

    private Double lon;

    private Double lat;

}
