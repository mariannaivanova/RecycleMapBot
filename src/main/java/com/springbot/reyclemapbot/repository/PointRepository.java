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
    @Query(value = "insert into points(id, address, title, geom, url) values (:id, :address, :title, st_setsrid(st_makepoint(:lon, :lat), 4326), :url)", nativeQuery = true)
    public void save(Long id, String address, String title, Double lon, Double lat, String url);
}
