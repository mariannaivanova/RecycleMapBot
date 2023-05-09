package com.springbot.recyclemapbot.config;

import java.util.HashMap;
import java.util.Map;

public class BotStateCash {
    private final Map<Long, BotState> botStateMap = new HashMap<>();

    public void saveBotState(Long chatId, BotState botState) {
        botStateMap.put(chatId, botState);
    }

}