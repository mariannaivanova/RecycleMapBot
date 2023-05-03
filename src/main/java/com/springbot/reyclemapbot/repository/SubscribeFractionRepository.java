package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.model.Fraction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public class SubscribeFractionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Modifying
    @Transactional
    public void saveSubscribeFraction(Long pointId, Set<Fraction> fractions) {
        for (Fraction fraction: fractions){
            entityManager.createNativeQuery("INSERT INTO points_fractions (point_id, fraction_id) VALUES (?,?)")
                    .setParameter(1, pointId)
                    .setParameter(2, fraction.getId())
                    .executeUpdate();
        }

    }
}
