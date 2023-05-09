/*package com.springbot.reyclemapbot.config;

public class LocationHandler {


    public void processLocation(Long chatId, Double lon, Double lat, BotState botState){
     //   botStateCash.saveBotState(userId, botState);
        //if state =...
        switch (botState.name()) {
            case ("SUBSCRIBE"):
                return menuService.getMainMenuMessage(message.getChatId(),
                        "Воспользуйтесь главным меню", userId);
            case ("APPLICATION"):
                //set time zone user. for correct sent event
                return eventHandler.enterLocalTimeUser(message);
            default:
                throw new IllegalStateException("Unexpected value: " + botState);
        }
    }
}*/
