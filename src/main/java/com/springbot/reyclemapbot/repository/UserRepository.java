package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.model.Points;
import com.springbot.reyclemapbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Transactional
    @Query(value = "select distinct chat_id from users", nativeQuery = true)
    List<Long> getAllChatIds();
}
