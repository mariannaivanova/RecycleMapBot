package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.model.Points;
import com.vividsolutions.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PointRepository extends JpaRepository<Points, Integer> {
    @Modifying
    @Transactional
    @Query(value = "insert into points(id, address, title, geom, url, restricted) " +
            "values (:id, :address, :title, st_setsrid(st_makepoint(:lon, :lat), 4326), :url, :restricted)" +
            "ON CONFLICT (id) DO UPDATE SET address = excluded.address, title = excluded.title, geom = excluded.geom, url = excluded.url, restricted = excluded.restricted", nativeQuery = true)
    public void save(Long id, String address, String title, Double lon, Double lat, String url, boolean restricted);


    @Transactional
    @Query(value = "SELECT ST_AsText(geom) from points\n" +
            "WHERE id in (SELECT id\n" +
            "from (SELECT DISTINCT id, address, ST_AsText(geom) AS geom,\n" +
            "       ST_DistanceSphere(geom, st_setsrid(st_makepoint(:lon, :lat), 4326)) AS dist\n" +
            "FROM points_fractions_view\n" +
            "WHERE fraction in :fractions\n" +
            "ORDER BY dist\n" +
            "LIMIT 10) a1\n" +
            "where a1.dist <= :dist)", nativeQuery = true)
    public List<String> getRec(Double lon, Double lat, Double dist, Set<String> fractions);



    @Transactional
    @Query(value = "select point_id \n" +
            "\tfrom points_history \n" +
            "\twhere updated = false and upper(valid_range) is NULL\n" +
            "\t\t\t\tand (now() - INTERVAL '1 day') > lower(valid_range) and point_id < 10", nativeQuery = true)
    public List<Long> getDeleted();

    @Modifying
    @Transactional
    @Query(value = "delete from points where id = :id", nativeQuery = true)
    public void deleteById(Long id);
}
