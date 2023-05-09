package com.springbot.recyclemapbot.repository;

import com.springbot.recyclemapbot.DTO.SubscribeString;
import com.springbot.recyclemapbot.model.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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
            "SELECT subscribe_id, f.id from ins1, fractions as f where name in :fractionIds", nativeQuery = true)
    public void saveSubscribe(Long chatId, Double lon, Double lat, Double dist, Set<String> fractionIds, Set<Long> pointIds);



    @Transactional
    @Query(value = "select id from subscribes where chat_id = :chatId", nativeQuery = true)
    public List<Long> getSubscribesByChatId(Long chatId);

    @Transactional
    @Query(value = "select id from subscribes", nativeQuery = true)
    public List<Long> getAllSubscribeIds();

    @Transactional
    @Query(value = "select id as id, chat_id as chatId, st_astext(geom) as location, dist as distance from subscribes where id = :id", nativeQuery = true)
    public SubscribeString getSubscribeById(Long id);

    @Modifying
    @Transactional
    @Query(value = "WITH del1 AS (\n" +
            "    DELETE from subscribes_points where subscribe_id = :id\n" +
            " RETURNING subscribe_id AS subscribe_id),\n" +
            "    del2 AS (\n" +
            "    DELETE from subscribes_fractions where subscribe_id = :id\n" +
            " RETURNING subscribe_id AS subscribe_id1)\n" +
            "delete from subscribes where id = :id", nativeQuery = true)
    public void deleteById(Long id);


/*    @Modifying
    @Transactional
    @Query(value = "insert into subscribes_points(subscribe_id, point_id) where select u FROM User u WHERE u.name IN :names", nativeQuery = true)
    public void saveSubscribePoints(Long subscribeId, List<Long> pointIds);*/

}
