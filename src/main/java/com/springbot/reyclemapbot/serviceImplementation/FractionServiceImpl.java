package com.springbot.reyclemapbot.serviceImplementation;

import com.springbot.reyclemapbot.model.Fraction;
import com.springbot.reyclemapbot.model.Points;
import com.springbot.reyclemapbot.repository.FractionRepository;
import com.springbot.reyclemapbot.repository.PointRepository;
import com.springbot.reyclemapbot.service.FractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FractionServiceImpl implements FractionService {

    private final FractionRepository fractionRepository;

    @Override
    public void save(Fraction fraction) {
        fractionRepository.save(fraction);
    }
}
