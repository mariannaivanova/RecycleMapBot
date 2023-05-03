package com.springbot.reyclemapbot.service;

import com.springbot.reyclemapbot.model.Fraction;
import com.springbot.reyclemapbot.model.Points;

import java.io.IOException;

public interface FractionService {
    void save() throws IOException;

    Fraction getFractionById(Integer id);
}
