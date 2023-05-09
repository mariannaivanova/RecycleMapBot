package com.springbot.recyclemapbot.service;

import com.springbot.recyclemapbot.model.Fraction;

import java.io.IOException;

public interface FractionService {
    void save() throws IOException;

    Fraction getFractionById(Integer id);
}
