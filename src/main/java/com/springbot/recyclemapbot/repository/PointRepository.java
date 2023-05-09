package com.springbot.recyclemapbot.repository;

import com.springbot.recyclemapbot.DTO.PointDTO;
import com.springbot.recyclemapbot.model.Points;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface PointRepository extends JpaRepository<Points, Integer> {

    /*@Query(value = "insert into points(id, address, title, geom, url, restricted) " +
            "values (:id, :address, :title, st_setsrid(st_makepoint(:lon, :lat), 4326), :url, :restricted)" +
            "ON CONFLICT (id) DO UPDATE SET address = excluded.address, title = excluded.title, geom = excluded.geom, url = excluded.url, restricted = excluded.restricted", nativeQuery = true)
    public void save(Long id, String address, String title, Double lon, Double lat, String url, boolean restricted);
*/
    @Modifying
    @Transactional
    @Query(value="WITH ins1 AS (\n" +
            "  insert into points(id, address, title, geom, url, restricted)\n" +
            "  values (:id, :address, :title, st_setsrid(st_makepoint(:lon, :lat), 4326), :url, :restricted)\n" +
            "  ON CONFLICT (id) DO UPDATE SET address = excluded.address, title = excluded.title, geom = excluded.geom, url = excluded.url, restricted = excluded.restricted\n" +
            "   RETURNING id AS point_id\n" +
            "   ), ins2 AS (\n" +
            "INSERT INTO points_fractions(point_id, fraction_id)\n" +
            "SELECT point_id, f.id from ins1, fractions as f where name in :fractions\n" +
            "AND\n" +
            "    NOT EXISTS (\n" +
            "        SELECT fraction_id FROM points_fractions WHERE point_id = :id)\n" +
            "RETURNING point_id as point)\n" +
            "   DELETE from points_fractions where point_id = :id and fraction_id not in (select id from fractions where name in :fractions)", nativeQuery = true)
    public void save(Long id, String address, String title, Double lon, Double lat, String url, boolean restricted, Set<String> fractions);

    @Transactional
    @Query(value = "SELECT id\n" +
            "from (SELECT DISTINCT id, address, ST_AsText(geom) AS geom,\n" +
            "       ST_DistanceSphere(geom, st_setsrid(st_makepoint(:lon, :lat), 4326)) AS dist\n" +
            "FROM points_fractions_view\n" +
            "WHERE fraction in :fractions and restricted = false\n" +
            "ORDER BY dist\n" +
            "LIMIT 5) a1\n" +
            "where a1.dist <= :dist", nativeQuery = true)
    public Set<Long> getRec(Double lon, Double lat, Double dist, Set<String> fractions);



    @Transactional
    @Query(value = "SELECT id\n" +
            "from (SELECT DISTINCT id, address, ST_AsText(geom) AS geom,\n" +
            "       ST_DistanceSphere(geom, st_setsrid(st_makepoint(:lon, :lat), 4326)) AS dist\n" +
            "FROM points_fractions_view\n" +
            "WHERE fraction in :fractions and restricted = false\n " +
            "ORDER BY dist\n" +
            "LIMIT 5) a1", nativeQuery = true)
    public Set<Long> getClosest(Double lon, Double lat, Set<String> fractions);

    @Transactional
    @Query(value = "select point_id \n" +
            "\tfrom points_history \n" +
            "\twhere updated = false and upper(valid_range) is NULL\n" +
            "\t\t\t\tand (now() - INTERVAL '1 day') > lower(valid_range) and point_id < 10", nativeQuery = true)
    public List<Long> getDeleted();


    @Transactional
    @Query(value = "SELECT p.id\n" +
            "    FROM subscribes s\n" +
            "         LEFT OUTER JOIN subscribes_points sp ON sp.subscribe_id = s.id\n" +
            "         LEFT OUTER JOIN points p ON p.id = sp.point_id where s.id = :id" +
            "    ORDER BY p.id",
            nativeQuery = true)
    public Set<Long> getPointsBySubscribeId(Long id);

    @Modifying
    @Transactional
    @Query(value = "delete from points where id = :id", nativeQuery = true)
    public void deleteById(Long id);


    @Transactional
    @Query(value = "SELECT updated\n" +
            "\tFROM public.points_history\n" +
            "\twhere point_id in :pointIds", nativeQuery = true)
    public List<Boolean> checkUpdates(Set<Long> pointIds);


    @Modifying
    @Transactional
    @Query(value = "UPDATE public.points_history\n" +
            "\tSET updated=:updated", nativeQuery = true)
    public void setUpdatedAll(boolean updated);

    @Transactional
    @Query(value = "SELECT address, title, url\n" +
            "\tFROM public.points " +
            "where id = :id", nativeQuery = true)
    public PointDTO getPointInfo(Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.points_history\n" +
            "\tSET updated=:updated where point_id = :id", nativeQuery = true)
    public void setUpdatedByPointId(Long id, boolean updated);


    @Transactional
    @Query(value = "SELECT p.address\n" +
            "    FROM subscribes s\n" +
            "         LEFT OUTER JOIN subscribes_points sp ON sp.subscribe_id = s.id\n" +
            "         LEFT OUTER JOIN points p ON p.id = sp.point_id where s.id = :id" +
            "    ORDER BY p.id",
            nativeQuery = true)
    public List<String> getPointsAddress(Long id);
}

/*
"WITH ins1 AS (\n" +
        "  insert into points(id, address, title, geom, url, restricted)\n" +
        "  values (:id, :address, :title, st_setsrid(st_makepoint(:lon, :lat), 4326), :url, :restricted)\n" +
        "  ON CONFLICT (id) DO UPDATE SET address = excluded.address, title = excluded.title, geom = excluded.geom, url = excluded.url, restricted = excluded.restricted\n" +
        "   RETURNING id AS point_id\n" +
        "   )\n" +
        "INSERT INTO points_fractions(point_id, fraction_id)\n" +
        "SELECT point_id, f.id from ins1, fractions as f where name in :fractions\n" +
        "AND\n" +
        "    NOT EXISTS (\n" +
        "        SELECT fraction_id FROM points_fractions WHERE point_id = :id\n" +
        "    )"*/
