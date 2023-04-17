package com.springbot.reyclemapbot.repository;

import com.springbot.reyclemapbot.model.Fraction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FractionRepository extends JpaRepository<Fraction, Integer> {
}
