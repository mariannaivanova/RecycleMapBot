package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.DTO.SubscribeDTO;
import com.springbot.reyclemapbot.model.Points;
import com.springbot.reyclemapbot.model.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;



public interface SubscribeRepository extends JpaRepository<Subscribe, Integer> {
    @Modifying
    @Transactional
    @Query(value = "insert into subscribes(chat_id, geom) values (:chatId, st_setsrid(st_makepoint(:lon, :lat), 4326))", nativeQuery = true)
    public void saveSubscribe(Long chatId, Double lon, Double lat);
}
