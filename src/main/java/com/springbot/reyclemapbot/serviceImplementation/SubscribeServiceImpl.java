package com.springbot.reyclemapbot.serviceImplementation;

import com.springbot.reyclemapbot.DTO.SubscribeDTO;
import com.springbot.reyclemapbot.repository.SubscribeRepository;
import com.springbot.reyclemapbot.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Location;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {

    private final SubscribeRepository subscribeRepository;

    public void save(Long chatId, Location location, Double dist){
        //SubscribeDTO subscribeDTO = new SubscribeDTO(chatId, location, dist);
        Double lon = location.getLongitude();
        Double lat = location.getLatitude();
        this.subscribeRepository.saveSubscribe(chatId, lon, lat, dist);
    }
}
