package com.springbot.recyclemapbot.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface PointService {
    void save() throws IOException;

    Set<Long> getRec(Double lon, Double lat, Double dist, Set<String> fractions);

    void delete(Long id);

    List<Long> getDeleted();

}
