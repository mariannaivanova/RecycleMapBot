package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.model.Points;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PointRepository extends JpaRepository<Points, Integer> {
    @Modifying
    @Transactional
    @Query(value = "insert into points(id, address, restricted, title, geom) values (:id, :address, :restricted, :title, st_setsrid(st_makepoint(:lon, :lat), 4326))", nativeQuery = true)
    public void save(Long id, String address, boolean restricted, String title, Double lon, Double lat);
}
