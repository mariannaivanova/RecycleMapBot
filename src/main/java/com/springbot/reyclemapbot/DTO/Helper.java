package com.springbot.reyclemapbot.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Data
public class Helper {

    private Double lon;

    private Double lat;

    private Double dist;

    private Set<String> fractions;
}
