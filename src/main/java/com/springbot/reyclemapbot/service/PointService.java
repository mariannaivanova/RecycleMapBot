package com.springbot.reyclemapbot.service;

import com.springbot.reyclemapbot.model.Points;

public interface PointService {
    void save(Long id, String address, boolean restricted, String title, Double lon, Double lat);

}
