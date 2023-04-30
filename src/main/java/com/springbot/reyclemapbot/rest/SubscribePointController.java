package com.springbot.reyclemapbot.rest;

import com.springbot.reyclemapbot.repository.PointFractionRepository;
import com.springbot.reyclemapbot.repository.SubscribePointRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class SubscribePointController {
    private final SubscribePointRepository subscribePointRepository;

    private  final PointFractionRepository pointFractionRepository;

    @RequestMapping(value = "/subscribePoint/{id}", method = RequestMethod.POST)
    public void saveSubscribePoints(@PathVariable("id") Long id) throws IOException {
        Set<Long> ids = new HashSet<>();
        ids.add(10L);
        ids.add(8L);
        this.subscribePointRepository.saveSubscribePoint(id, ids);
    }

    @RequestMapping(value = "/pointFraction/{id}", method = RequestMethod.POST)
    public void savePointFractions(@PathVariable("id") Long id) throws IOException {
        Set<Integer> ids = new HashSet<>();
        ids.add(1);
        ids.add(2);
        this.pointFractionRepository.savePointFraction(id, ids);
    }
}
