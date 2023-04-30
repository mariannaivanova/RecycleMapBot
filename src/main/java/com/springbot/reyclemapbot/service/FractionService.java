package com.springbot.reyclemapbot.service;

import com.springbot.reyclemapbot.model.Fraction;
import com.springbot.reyclemapbot.model.Points;

public interface FractionService {
    void save(Fraction fraction);

    Fraction getFractionById(Integer id);
}
