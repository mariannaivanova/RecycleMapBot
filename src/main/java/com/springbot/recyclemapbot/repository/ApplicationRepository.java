package com.springbot.recyclemapbot.repository;

import com.springbot.recyclemapbot.DTO.ApplicationDTO;
import com.springbot.recyclemapbot.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    @Transactional
    @Query(value = "select id as id, st_astext(geom) as location, title as title from applications where chat_id = :chatId", nativeQuery = true)
    public List<ApplicationDTO> getApplicationDTOByChatId(Long chatId);



    @Modifying
    @Transactional
    @Query(value = "WITH ins1 AS (\n" +
            "   INSERT INTO applications(chat_id, geom, title)\n" +
            "   VALUES (:chat_id, st_setsrid(st_makepoint(:lon, :lat), 4326), :title)\n" +
            "-- ON     CONFLICT DO NOTHING         -- optional addition in Postgres 9.5+\n" +
            "   RETURNING id AS application_id\n" +
            "   )\n" +
            "INSERT INTO applications_fractions(application_id, fraction_id)\n" +
            "SELECT application_id, f.id from ins1, fractions as f where name in :fractions", nativeQuery = true)
    public void saveApplication(Long chat_id, Double lon, Double lat, String title, Set<String> fractions);

}
