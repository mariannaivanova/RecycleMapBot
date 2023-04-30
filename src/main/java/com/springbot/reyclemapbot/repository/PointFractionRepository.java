package com.springbot.reyclemapbot.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public class PointFractionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Modifying
    @Transactional
    public void savePointFraction(Long pointId, Set<Integer> fractionIds) {
        for (Integer
                fractionId: fractionIds){
            entityManager.createNativeQuery("INSERT INTO points_fractions (point_id, fraction_id) VALUES (?,?)")
                    .setParameter(1, pointId)
                    .setParameter(2, fractionId)
                    .executeUpdate();
        }

    }
}
