package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.DTO.ApplicationDTO;
import com.springbot.reyclemapbot.model.Application;
import com.springbot.reyclemapbot.model.Fraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    @Transactional
    @Query(value = "select chat_id, title from applications where chat_id = :chatId", nativeQuery = true)
    public List<ApplicationDTO> getApplicationDTOByChatId(Long chatId);

    @Query(value = "WITH ins1 AS (\n" +
            "   INSERT INTO applications(chat_id, title)\n" +
            "   VALUES (:chat_id, st_setsrid(st_makepoint(:lon, :lat), 4326), :title)\n" +
            "-- ON     CONFLICT DO NOTHING         -- optional addition in Postgres 9.5+\n" +
            "   RETURNING id AS application_id\n" +
            "   )\n" +
            "INSERT INTO applications_fractions(application_id, fraction_id)\n" +
            "SELECT application_id, f.id from ins1, fractions as f where f.id in :fractions", nativeQuery = true)
    public void saveApplication(Long chat_id, Double lon, Double lat, String title, Set<Integer> fractions);


}
