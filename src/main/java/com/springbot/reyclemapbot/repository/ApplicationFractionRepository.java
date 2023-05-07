package com.springbot.reyclemapbot.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public class ApplicationFractionRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Modifying
    @Transactional
    public void saveApplicationFraction(Long applicationId, Set<Integer> fractionIds) {
        for (Integer fractionId: fractionIds){
            entityManager.createNativeQuery("INSERT INTO applications_fractions (application_id, fraction_id) VALUES (?,?)")
                    .setParameter(1, applicationId)
                    .setParameter(2, fractionId)
                    .executeUpdate();
        }

    }
}
