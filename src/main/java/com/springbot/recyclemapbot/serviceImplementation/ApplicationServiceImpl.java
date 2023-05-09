package com.springbot.recyclemapbot.serviceImplementation;

import com.springbot.recyclemapbot.DTO.ApplicationDTO;
import com.springbot.recyclemapbot.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl {
    private final ApplicationRepository applicationRepository;

    public void save(Long chatId, Double lon, Double lat, String title, Set<String> fractions){
        this.applicationRepository.saveApplication(chatId, lon, lat, title, fractions);

    }

    public List<ApplicationDTO> getApplicationInfo(Long id){
        return this.applicationRepository.getApplicationDTOByChatId(id);
    }
}
