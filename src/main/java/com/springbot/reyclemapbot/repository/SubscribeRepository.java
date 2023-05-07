package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.DTO.SubscribeDTO;
import com.springbot.reyclemapbot.DTO.SubscribeString;
import com.springbot.reyclemapbot.model.Points;
import com.springbot.reyclemapbot.model.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public interface SubscribeRepository extends JpaRepository<Subscribe, Integer> {
    @Modifying
    @Transactional
    @Query(value = "WITH ins1 AS (\n" +
            "   insert into subscribes(chat_id, geom, dist) values (:chatId, st_setsrid(st_makepoint(:lon, :lat), 4326), :dist)\n" +
            "   RETURNING id AS subscribe_id\n" +
            "   ),  ins2 AS (\n" +
            "   INSERT INTO subscribes_points (subscribe_id, point_id)\n" +
            "\t SELECT subscribe_id, p.id FROM ins1, points as p where id in :pointIds\n" +
            "   RETURNING subscribe_id as sb\n" +
            "   )\n" +
            "INSERT INTO subscribes_fractions(subscribe_id, fraction_id)\n" +
            "SELECT subscribe_id, f.id from ins1, fractions as f where id in :fractionIds", nativeQuery = true)
    public void saveSubscribe(Long chatId, Double lon, Double lat, Double dist, Set<Integer> fractionIds, Set<Long> pointIds);



    @Transactional
    @Query(value = "select id from subscribes where chat_id = :chatId", nativeQuery = true)
    public Long getSubscribeByChatId(Long chatId);

    @Transactional
    @Query(value = "select id from subscribes", nativeQuery = true)
    public List<Long> getAllSubscribeIds();

    @Transactional
    @Query(value = "select chat_id, st_astext(geom) as location, dist from subscribes where id = :id", nativeQuery = true)
    public SubscribeString getSubscribeById(Long id);


/*    @Modifying
    @Transactional
    @Query(value = "insert into subscribes_points(subscribe_id, point_id) where select u FROM User u WHERE u.name IN :names", nativeQuery = true)
    public void saveSubscribePoints(Long subscribeId, List<Long> pointIds);*/

}
