package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.model.Points;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class SubscribePointRepositoryTest {

   /* @Test
    void insertWithQuery() {
        SubscribePointRepository subscribePointRepository = new SubscribePointRepository();
        Long subscribeId = 1L;
        Set<Long> points = new HashSet<>();
        points.add(5L);
        points.add(7L);
        assertThatExceptionOfType(PersistenceException.class).isThrownBy(() -> {
            subscribePointRepository.insertWithQuery(subscribeId, points);
        });
    }*/
}