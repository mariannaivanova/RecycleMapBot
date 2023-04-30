package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.model.Points;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public class SubscribePointRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Modifying
    @Transactional
    public void saveSubscribePoint(Long subscribeId, Set<Long> points) {
        for (Long point: points){
            entityManager.createNativeQuery("INSERT INTO subscribes_points (subscribe_id, point_id) VALUES (?,?)")
                    .setParameter(1, subscribeId)
                    .setParameter(2, point)
                    .executeUpdate();
        }

    }
}
