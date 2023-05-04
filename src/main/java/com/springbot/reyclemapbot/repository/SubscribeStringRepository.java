package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.DTO.SubscribeString;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
@Repository
public class SubscribeStringRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Modifying
    @Transactional
    public SubscribeString getSubscribeById(Long subscribeId) {
        Query query = entityManager.createNativeQuery("select st_astext(geom) as location, dist from subscribes where id = ?", SubscribeString.class).setParameter(1, subscribeId);
        SubscribeString subscribeString = new SubscribeString(query.getResultList().get(0).toString(), new Double(query.getResultList().get(1).toString()));
        return subscribeString;
        }
}
*/
