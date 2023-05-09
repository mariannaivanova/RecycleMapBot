package com.springbot.recyclemapbot.serviceImplementation;

import com.springbot.recyclemapbot.DTO.SubscribeString;
import com.springbot.recyclemapbot.repository.SubscribePointRepository;
import com.springbot.recyclemapbot.repository.SubscribeRepository;
//import com.springbot.reyclemapbot.repository.SubscribeStringRepository;
import com.springbot.recyclemapbot.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {

    private final SubscribeRepository subscribeRepository;


    private final SubscribePointRepository subscribePointRepository;


    public void save(Long chatId, Double lon, Double lat, Double dist, Set<String> fractionIds, Set<Long> pointIds){
        //выставляем фракции по умолчанию для поиска только по геопозиции
        if (fractionIds.isEmpty()){
            fractionIds.add("BUMAGA");
            fractionIds.add("PLASTIK");
            fractionIds.add("STEKLO");
            fractionIds.add("BATAREJKI");
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

    public List<Long> getSubscribesForUser(Long chatId){
        return this.subscribeRepository.getSubscribesByChatId(chatId);
    }

    public void delete(Long id){
        this.subscribeRepository.deleteById(id);
    }
}
