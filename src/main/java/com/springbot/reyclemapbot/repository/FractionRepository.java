package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.model.Fraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface FractionRepository extends JpaRepository<Fraction, Integer> {
    Fraction getFractionById(Integer id);

    @Transactional
    @Query(value = "SELECT f.name\n" +
            "    FROM subscribes s\n" +
            "         LEFT OUTER JOIN subscribes_fractions sf ON sf.subscribe_id = s.id\n" +
            "         LEFT OUTER JOIN fractions f ON f.id = sf.fraction_id where s.id = :id",
            nativeQuery = true)
    public Set<String> getFractionIdsBySubscribeId(Long id);
}
