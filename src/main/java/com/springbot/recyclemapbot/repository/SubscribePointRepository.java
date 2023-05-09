package com.springbot.recyclemapbot.repository;

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

    @Modifying
    @Transactional
    public void deleteSubscribePoint(Long subscribeId) {

            entityManager.createNativeQuery("DELETE from subscribes_points where subscribe_id = ?")
                    .setParameter(1, subscribeId)
                    .executeUpdate();

    }
}
