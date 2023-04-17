package com.springbot.reyclemapbot.serviceImplementation;

import com.springbot.reyclemapbot.model.Points;
import com.springbot.reyclemapbot.repository.PointRepository;
import com.springbot.reyclemapbot.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;

    @Override
    public void save(Long id, String address, boolean restricted, String title, Double lon, Double lat) {
        pointRepository.save(id, address, restricted, title, lon, lat);
    }
}
