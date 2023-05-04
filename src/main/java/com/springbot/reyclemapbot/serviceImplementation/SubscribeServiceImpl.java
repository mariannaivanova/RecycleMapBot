package com.springbot.reyclemapbot.serviceImplementation;

import com.springbot.reyclemapbot.DTO.SubscribeDTO;
import com.springbot.reyclemapbot.DTO.SubscribeString;
import com.springbot.reyclemapbot.model.Fraction;
import com.springbot.reyclemapbot.repository.SubscribeFractionRepository;
import com.springbot.reyclemapbot.repository.SubscribeRepository;
//import com.springbot.reyclemapbot.repository.SubscribeStringRepository;
import com.springbot.reyclemapbot.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {

    private final SubscribeRepository subscribeRepository;

    private final SubscribeFractionRepository subscribeFractionRepository;

//    private final SubscribeStringRepository subscribeStringRepository;

    public void save(Long chatId, Location location, Double dist, Set<Fraction> fractions){
        //SubscribeDTO subscribeDTO = new SubscribeDTO(chatId, location, dist);
        Double lon = location.getLongitude();
        Double lat = location.getLatitude();
        this.subscribeRepository.saveSubscribe(chatId, lon, lat, dist);
        Long id = this.subscribeRepository.getSubscribeByChatId(chatId).getId();
        this.subscribeFractionRepository.saveSubscribeFraction(id, fractions);
    }

    public SubscribeString getSubscribeById(Long id){
        return this.subscribeRepository.getSubscribeById(id);
    }
}
