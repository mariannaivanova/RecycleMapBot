package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.model.Points;
import com.springbot.reyclemapbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
