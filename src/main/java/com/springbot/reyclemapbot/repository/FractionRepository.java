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

    @Transactional
    @Query(value = "SELECT f.name\n" +
            "    FROM points p\n" +
            "         LEFT OUTER JOIN points_fractions pf ON pf.point_id = p.id\n" +
            "         LEFT OUTER JOIN fractions f ON f.id = pf.fraction_id where p.id = :id",
            nativeQuery = true)
    public Set<String> getFractionIdsByPointId(Long id);
}
