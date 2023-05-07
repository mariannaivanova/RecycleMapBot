package com.springbot.reyclemapbot.service;

import com.springbot.reyclemapbot.DTO.Helper;
import com.springbot.reyclemapbot.model.Fraction;
import com.springbot.reyclemapbot.model.Points;
import com.vividsolutions.jts.geom.Point;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

public interface PointService {
    void save() throws IOException;

    Set<Long> getRec(Double lon, Double lat, Double dist, Set<String> fractions);

    void delete(Long id);

    List<Long> getDeleted();

}
