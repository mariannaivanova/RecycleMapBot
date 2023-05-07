package com.springbot.reyclemapbot.serviceImplementation;

import com.springbot.reyclemapbot.DTO.SubscribeDTO;
import com.springbot.reyclemapbot.DTO.SubscribeString;
import com.springbot.reyclemapbot.model.Fraction;
import com.springbot.reyclemapbot.repository.SubscribePointRepository;
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


    private final SubscribePointRepository subscribePointRepository;


    public void save(Long chatId, Double lon, Double lat, Double dist, Set<Integer> fractionIds, Set<Long> pointIds){
        //выставляем фракции по умолчанию для поиска только по геопозиции
        if (fractionIds.isEmpty()){
            fractionIds.add(1);
            fractionIds.add(2);
            fractionIds.add(3);
            fractionIds.add(7);
        }
        this.subscribeRepository.saveSubscribe(chatId, lon, lat, dist, fractionIds, pointIds);
    }

    public void deleteSubscribePoint(Long subscribeId){
        this.subscribePointRepository.deleteSubscribePoint(subscribeId);
    }

    public void saveSubscribePoint(Long subscribeId, Set<Long> pointIds){
        this.subscribePointRepository.saveSubscribePoint(subscribeId, pointIds);
    }

    public SubscribeString getSubscribeById(Long id){
        return this.subscribeRepository.getSubscribeById(id);
    }
}
