package com.springbot.reyclemapbot.serviceImplementation;

import com.springbot.reyclemapbot.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl {
    private final ApplicationRepository applicationRepository;

    public void save(Long chatId, Double lon, Double lat, String title, Set<Integer> fractions){
        this.applicationRepository.saveApplication(chatId, lon, lat, title, fractions);

    }
}
